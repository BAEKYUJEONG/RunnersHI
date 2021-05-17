package com.ssafy.runnershi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

  @Value("${spring.redis.port}")
  private int port;

  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.password}")
  private String pwd;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(host);
    redisStandaloneConfiguration.setPort(port);
    redisStandaloneConfiguration.setPassword(pwd);
    LettuceConnectionFactory lettuceConnectionFactory =
        new LettuceConnectionFactory(redisStandaloneConfiguration);
    return lettuceConnectionFactory;
  }

  @Bean
  public RedisTemplate<?, ?> redisTemplate() {
    RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }

  @Bean
  public ZSetOperations<String, String> zSetOperations(
      RedisTemplate<String, String> redisTemplate) {
    return redisTemplate.opsForZSet();
  }

  @Bean
  public SetOperations<String, String> SetOperations(RedisTemplate<String, String> redisTemplate) {
    return redisTemplate.opsForSet();
  }

}
