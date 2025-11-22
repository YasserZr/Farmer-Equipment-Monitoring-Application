package com.farm.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application.
 * 
 * This gateway provides:
 * - Routing to backend microservices
 * - Load balancing via Eureka service discovery
 * - CORS configuration for frontend
 * - Rate limiting per user/IP
 * - Circuit breaker integration
 * - Request/response logging
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
