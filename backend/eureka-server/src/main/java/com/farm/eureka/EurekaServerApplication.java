package com.farm.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server for Service Discovery.
 * 
 * This server acts as a registry where all microservices register themselves
 * and discover other services. It provides:
 * - Service registration and discovery
 * - Health monitoring
 * - Load balancing information
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
