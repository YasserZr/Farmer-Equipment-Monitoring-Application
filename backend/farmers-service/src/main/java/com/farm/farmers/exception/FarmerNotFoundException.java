package com.farm.farmers.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested farmer is not found in the system.
 * This is a domain-specific exception that indicates the farmer entity does not exist.
 */
public class FarmerNotFoundException extends RuntimeException {
    
    private final UUID farmerId;
    
    /**
     * Constructs a new FarmerNotFoundException with a farmer ID
     * @param farmerId the ID of the farmer that was not found
     */
    public FarmerNotFoundException(UUID farmerId) {
        super(String.format("Farmer not found with ID: %s", farmerId));
        this.farmerId = farmerId;
    }
    
    /**
     * Constructs a new FarmerNotFoundException with an email
     * @param email the email of the farmer that was not found
     */
    public FarmerNotFoundException(String email) {
        super(String.format("Farmer not found with email: %s", email));
        this.farmerId = null;
    }
    
    /**
     * Constructs a new FarmerNotFoundException with a custom message
     * @param message the detail message
     * @param farmerId the ID of the farmer that was not found
     */
    public FarmerNotFoundException(String message, UUID farmerId) {
        super(message);
        this.farmerId = farmerId;
    }
    
    /**
     * Constructs a new FarmerNotFoundException with a custom message and cause
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public FarmerNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.farmerId = null;
    }
    
    /**
     * Get the farmer ID associated with this exception
     * @return the farmer ID, or null if not applicable
     */
    public UUID getFarmerId() {
        return farmerId;
    }
}
