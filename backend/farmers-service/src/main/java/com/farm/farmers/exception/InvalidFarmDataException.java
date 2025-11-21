package com.farm.farmers.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when farm data validation fails.
 * This exception can carry validation error details for multiple fields.
 */
public class InvalidFarmDataException extends RuntimeException {
    
    private final Map<String, String> validationErrors;
    
    /**
     * Constructs a new InvalidFarmDataException with a simple message
     * @param message the detail message
     */
    public InvalidFarmDataException(String message) {
        super(message);
        this.validationErrors = new HashMap<>();
    }
    
    /**
     * Constructs a new InvalidFarmDataException with validation errors
     * @param message the detail message
     * @param validationErrors map of field names to error messages
     */
    public InvalidFarmDataException(String message, Map<String, String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors != null ? new HashMap<>(validationErrors) : new HashMap<>();
    }
    
    /**
     * Constructs a new InvalidFarmDataException with a single field error
     * @param message the detail message
     * @param fieldName the name of the invalid field
     * @param fieldError the error message for the field
     */
    public InvalidFarmDataException(String message, String fieldName, String fieldError) {
        super(message);
        this.validationErrors = new HashMap<>();
        this.validationErrors.put(fieldName, fieldError);
    }
    
    /**
     * Constructs a new InvalidFarmDataException with a cause
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public InvalidFarmDataException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = new HashMap<>();
    }
    
    /**
     * Get the validation errors map
     * @return map of field names to error messages
     */
    public Map<String, String> getValidationErrors() {
        return new HashMap<>(validationErrors);
    }
    
    /**
     * Check if there are any validation errors
     * @return true if validation errors exist
     */
    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
    }
    
    /**
     * Add a validation error for a specific field
     * @param fieldName the field name
     * @param errorMessage the error message
     */
    public void addValidationError(String fieldName, String errorMessage) {
        this.validationErrors.put(fieldName, errorMessage);
    }
    
    /**
     * Get error message for a specific field
     * @param fieldName the field name
     * @return the error message, or null if not found
     */
    public String getFieldError(String fieldName) {
        return validationErrors.get(fieldName);
    }
    
    /**
     * Get a formatted string of all validation errors
     * @return formatted validation errors
     */
    public String getFormattedErrors() {
        if (validationErrors.isEmpty()) {
            return getMessage();
        }
        
        StringBuilder sb = new StringBuilder(getMessage());
        sb.append(" - Validation errors: ");
        validationErrors.forEach((field, error) -> 
            sb.append(String.format("[%s: %s] ", field, error))
        );
        return sb.toString();
    }
}
