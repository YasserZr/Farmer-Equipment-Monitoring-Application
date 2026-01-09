package com.farm.supervision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for Supervision Service.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SupervisionServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SupervisionServiceApplication.class, args);
    }
}
