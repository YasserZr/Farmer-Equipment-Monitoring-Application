package com.farm.equipment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for Equipment Service.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class EquipmentServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EquipmentServiceApplication.class, args);
    }
}
