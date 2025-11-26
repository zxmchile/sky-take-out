package com.sky.config;

import io.lettuce.core.dynamic.RedisCommandFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description: Redis数据库配置类
 */
@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建RedisTemplate对象...");
        RedisTemplate redisTemplate = new RedisTemplate(); // 创建RedisTemplate对象
        redisTemplate.setConnectionFactory(redisConnectionFactory); // 设置RedisConnectionFactory
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 设置key的序列化方式
        return redisTemplate;
    }
}
