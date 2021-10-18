package com.lyra.api.config;

import com.lyra.utils.RedisOperator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisOperatorConfig {
    @Bean
    public RedisOperator redisOperator() {
        return new RedisOperator();
    }
}
