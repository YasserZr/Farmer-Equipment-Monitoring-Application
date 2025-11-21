package com.farm.equipment.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Feign client for communicating with the Farmers microservice.
 */
@FeignClient(name = "farmers-service", fallback = FarmersFeignClientFallback.class)
public interface FarmersFeignClient {
    
    /**
     * Check if a farmer has permission to perform an action on a resource
     * @param farmerId the farmer ID
     * @param resourceId the resource ID (equipment ID)
     * @param action the action to perform
     * @return permission check response
     */
    @GetMapping("/api/farmers/{farmerId}/permissions")
    @CircuitBreaker(name = "farmersService", fallbackMethod = "checkPermissionFallback")
    PermissionCheckResponse checkPermission(
        @PathVariable UUID farmerId,
        @RequestParam UUID resourceId,
        @RequestParam String action
    );
    
    /**
     * Check if a farmer exists
     * @param farmerId the farmer ID
     * @return true if farmer exists
     */
    @GetMapping("/api/farmers/{farmerId}/exists")
    @CircuitBreaker(name = "farmersService", fallbackMethod = "farmerExistsFallback")
    Boolean farmerExists(@PathVariable UUID farmerId);
}
