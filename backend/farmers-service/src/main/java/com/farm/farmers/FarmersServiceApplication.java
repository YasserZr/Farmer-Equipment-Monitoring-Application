package com.farm.farmers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FarmersServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmersServiceApplication.class, args);
    }
}
