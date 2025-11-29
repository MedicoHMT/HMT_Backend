package com.example.hmt.core.auth.service;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> cmd;
    private final int refreshTtlSeconds;

    public RefreshTokenService(org.springframework.core.env.Environment env,
                               @Value("${jwt.refresh.expiration.seconds}") int refreshTtlSeconds) {
        String uri = env.getProperty("otp.redis.uri", "redis://localhost:6379");
        this.redisClient = RedisClient.create(uri);
        this.connection = redisClient.connect();
        this.cmd = connection.sync();
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public String createRefreshForUser(String userEmail) {
        String jti = UUID.randomUUID().toString();
        String key = keyOf(jti);
        // store userEmail as value; in prod store JSON (userId, ip, ua) and sign/encrypt if needed
        cmd.setex(key, refreshTtlSeconds, userEmail);
        return jti;
    }

    public Optional<String> validateRefresh(String jti) {
        if (jti == null || jti.isBlank()) return Optional.empty();
        String val = cmd.get(keyOf(jti));
        return Optional.ofNullable(val);
    }

    public Optional<String> rotate(String oldJti) {
        // atomically: get value, delete old, create new with same value
        String keyOld = keyOf(oldJti);
        String userEmail = cmd.get(keyOld);
        if (userEmail == null) return Optional.empty();

        // delete old
        cmd.del(keyOld);

        // create new jti
        String newJti = UUID.randomUUID().toString();
        cmd.setex(keyOf(newJti), refreshTtlSeconds, userEmail);
        return Optional.of(newJti);
    }

    public void revoke(String jti) {
        if (jti == null) return;
        cmd.del(keyOf(jti));
    }

    public boolean isRevokedAccessJti(String accessJti) {
        return cmd.exists("revoked:" + accessJti) > 0;
    }

//    public void revokeAllForUser(String userEmail) {
//        // Optional: for safety, you can iterate keys pattern refresh:* and delete those matching value.
//        // This is O(N) and may be heavy — better store reverse index in production.
//        var keys = cmd.keys("refresh:*");
//        for (String k : keys) {
//            String v = cmd.get(k);
//            if (userEmail.equals(v)) cmd.del(k);
//        }
//    }

    private String keyOf(String jti) {
        return "refresh:" + jti;
    }

    @PreDestroy
    public void close() {
        connection.close();
        redisClient.shutdown();
    }
}