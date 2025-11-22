package com.farm.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback controller for circuit breaker.
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {
    
    @GetMapping("/farmers")
    public ResponseEntity<Map<String, Object>> farmersFallback() {
        log.warn("Farmers service is unavailable - returning fallback response");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackResponse("Farmers Service"));
    }
    
    @GetMapping("/equipment")
    public ResponseEntity<Map<String, Object>> equipmentFallback() {
        log.warn("Equipment service is unavailable - returning fallback response");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackResponse("Equipment Service"));
    }
    
    @GetMapping("/supervision")
    public ResponseEntity<Map<String, Object>> supervisionFallback() {
        log.warn("Supervision service is unavailable - returning fallback response");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackResponse("Supervision Service"));
    }
    
    private Map<String, Object> createFallbackResponse(String serviceName) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", serviceName + " is currently unavailable. Please try again later.");
        response.put("service", serviceName);
        return response;
    }
}
