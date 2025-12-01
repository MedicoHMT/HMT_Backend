package com.example.hmt.core.config;


import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    @ConditionalOnProperty(name = "otp.redis.enabled", havingValue = "true")
    public RedisClient redisClient(org.springframework.core.env.Environment env) {
        return RedisClient.create(env.getProperty("otp.redis.uri", "redis://localhost:6379"));
    }

    @Bean
    @ConditionalOnProperty(name = "otp.redis.enabled", havingValue = "true")
    public StatefulRedisConnection<String, String> statefulRedisConnection(RedisClient client) {
        return client.connect();
    }
}
