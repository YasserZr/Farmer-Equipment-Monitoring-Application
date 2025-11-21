package com.farm.equipment.exception;

import java.util.UUID;

/**
 * Exception thrown when a pump is not found.
 */
public class PumpNotFoundException extends RuntimeException {
    
    public PumpNotFoundException(UUID pumpId) {
        super(String.format("Pump not found with ID: %s", pumpId));
    }
}
