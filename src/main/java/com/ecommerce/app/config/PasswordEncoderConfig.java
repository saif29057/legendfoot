package com.ecommerce.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration class for password encoding.
 * 
 * This class is separated from SecurityConfig to avoid circular dependencies
 * between UserService and SecurityConfig.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Password encoder bean using BCrypt.
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
