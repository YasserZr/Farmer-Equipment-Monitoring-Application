package com.farm.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server for centralized configuration management.
 * 
 * This server provides:
 * - Centralized configuration for all microservices
 * - Environment-specific configurations (dev, prod)
 * - Git-based configuration repository
 * - Encryption for sensitive properties
 * - Configuration refresh without restart
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
