package com.farm.equipment.exception;

/**
 * Exception thrown when permission check fails.
 */
public class PermissionDeniedException extends RuntimeException {
    
    public PermissionDeniedException(String message) {
        super(message);
    }
}
