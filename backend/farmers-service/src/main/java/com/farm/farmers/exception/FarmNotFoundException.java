package com.farm.farmers.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested farm is not found in the system.
 * This is a domain-specific exception that indicates the farm entity does not exist.
 */
public class FarmNotFoundException extends RuntimeException {
    
    private final UUID farmId;
    
    /**
     * Constructs a new FarmNotFoundException with a farm ID
     * @param farmId the ID of the farm that was not found
     */
    public FarmNotFoundException(UUID farmId) {
        super(String.format("Farm not found with ID: %s", farmId));
        this.farmId = farmId;
    }
    
    /**
     * Constructs a new FarmNotFoundException with a custom message
     * @param message the detail message
     * @param farmId the ID of the farm that was not found
     */
    public FarmNotFoundException(String message, UUID farmId) {
        super(message);
        this.farmId = farmId;
    }
    
    /**
     * Constructs a new FarmNotFoundException with a custom message and cause
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public FarmNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.farmId = null;
    }
    
    /**
     * Get the farm ID associated with this exception
     * @return the farm ID, or null if not applicable
     */
    public UUID getFarmId() {
        return farmId;
    }
}
