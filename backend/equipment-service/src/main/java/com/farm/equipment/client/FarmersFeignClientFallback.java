package com.farm.equipment.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Fallback implementation for FarmersFeignClient.
 * Provides default responses when Farmers service is unavailable.
 */
@Component
@Slf4j
public class FarmersFeignClientFallback implements FarmersFeignClient {
    
    @Override
    public PermissionCheckResponse checkPermission(UUID farmerId, UUID resourceId, String action) {
        log.warn("Farmers service unavailable, using fallback for permission check");
        return PermissionCheckResponse.builder()
                .farmerId(farmerId)
                .resourceId(resourceId)
                .action(action)
                .allowed(false)
                .reason("Farmers service is currently unavailable. Please try again later.")
                .build();
    }
    
    @Override
    public Boolean farmerExists(UUID farmerId) {
        log.warn("Farmers service unavailable, using fallback for farmer existence check");
        // Conservative approach: assume farmer doesn't exist when service is down
        return false;
    }
}
