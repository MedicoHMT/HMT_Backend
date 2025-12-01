package com.example.hmt.core.otp;


import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.stereotype.Component;

@Component
public class RedisOtpStore implements OtpStore {

    private final RedisClient client;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> cmd;

    public RedisOtpStore(org.springframework.core.env.Environment env) {
        String uri = env.getProperty("otp.redis.uri", "redis://localhost:6379");
        this.client = RedisClient.create(uri);
        this.connection = client.connect();
        this.cmd = connection.sync();
    }

    // Note: keys should be composed by caller (tenant/identifier). We keep callers simple:
    @Override
    public boolean isLocked(String key) {
        return cmd.exists("lock:" + key) > 0;
    }

    @Override
    public long incrRequestCount(String key, int ttlSeconds) {
        String redisKey = "otp:reqs:" + key;
        long val = cmd.incr(redisKey);
        if (val == 1) cmd.expire(redisKey, ttlSeconds);
        return val;
    }

    @Override
    public long incrIpCount(String key, int ttlSeconds) {
        String redisKey = "otp:reqs:ip:" + key;
        long val = cmd.incr(redisKey);
        if (val == 1) cmd.expire(redisKey, ttlSeconds);
        return val;
    }

    @Override
    public void storeOtpHash(String key, String hash, int ttlSeconds) {
        String redisKey = "otp:" + key;
        // set with TTL (overwrite allowed)
        cmd.setex(redisKey, ttlSeconds, hash);
    }

    @Override
    public String getOtpHash(String key) {
        return cmd.get("otp:" + key);
    }

    @Override
    public void deleteOtp(String key) {
        cmd.del("otp:" + key);
    }

    @Override
    public long incrAttempts(String key, int ttlSeconds) {
        String redisKey = "otp:attempts:" + key;
        long val = cmd.incr(redisKey);
        if (val == 1) cmd.expire(redisKey, ttlSeconds);
        return val;
    }

    @Override
    public void setLock(String key, int ttlSeconds) {
        cmd.setex("lock:" + key, ttlSeconds, "1");
    }

    // cleanup method if app shuts down
    public void close() {
        connection.close();
        client.shutdown();
    }
}
