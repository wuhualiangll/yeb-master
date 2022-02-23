package com.wu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {

        RedisTemplate<String,Object> redisTemplate  = new RedisTemplate<>();
        //为String 的key的key设置序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //为String的value设置序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //为hash的key设置序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //为Hash的value设置序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        //放进工厂
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return  redisTemplate;

    }
    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration(){
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration()
               //主节点的名称
                .master("mymaster")
                //哨兵
                .sentinel("120.76.134.149",26379)
                .sentinel("120.76.134.149",26380)
                .sentinel("120.76.134.149",26381);
        //密码
        redisSentinelConfiguration.setPassword("root");
        return redisSentinelConfiguration;
  }
}