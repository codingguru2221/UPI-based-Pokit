package com.pokit.upipocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.pokit.upipocket.repository")
public class DatabaseConfig {
    // Database configuration is handled by Spring Boot auto-configuration
    // This class is here to explicitly enable JPA repositories if needed
}