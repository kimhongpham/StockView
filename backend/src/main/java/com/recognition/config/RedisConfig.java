package com.recognition.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis configuration for market data caching.
 * Only initialized when cache is enabled in application properties.
 */
@Configuration
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class RedisConfig {

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    log.info("✓ Initializing Redis connection factory (Lettuce)");
    return new LettuceConnectionFactory();
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    log.info("✓ Configuring RedisTemplate with JSON serialization");

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Configure Jackson ObjectMapper for JSON serialization
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    objectMapper.activateDefaultTyping(
        BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType(Object.class)
            .build(),
        ObjectMapper.DefaultTyping.NON_FINAL);

    GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(
        objectMapper);

    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    // Set key-value serializers
    template.setKeySerializer(stringRedisSerializer);
    template.setValueSerializer(jackson2JsonRedisSerializer);

    // Set hash key-value serializers
    template.setHashKeySerializer(stringRedisSerializer);
    template.setHashValueSerializer(jackson2JsonRedisSerializer);

    template.afterPropertiesSet();

    log.info("✓ RedisTemplate configured with JSON serialization");
    return template;
  }
}
