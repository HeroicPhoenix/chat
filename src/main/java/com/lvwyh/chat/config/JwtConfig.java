package com.lvwyh.chat.config;

import com.lvwyh.chat.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置
 */
@Configuration
public class JwtConfig {

    @Value("${chat.jwt.secret}")
    private String secret;

    @Value("${chat.jwt.expire-seconds}")
    private long expireSeconds;

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil(secret, expireSeconds);
    }
}