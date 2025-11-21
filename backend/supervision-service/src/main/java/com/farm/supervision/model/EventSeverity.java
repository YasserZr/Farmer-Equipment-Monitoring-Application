package com.farm.supervision.model;

/**
 * Enum representing event severity levels.
 */
public enum EventSeverity {
    INFO,
    WARNING,
    CRITICAL;
    
    /**
     * Determine if this severity requires immediate attention
     * @return true for WARNING and CRITICAL
     */
    public boolean requiresAttention() {
        return this == WARNING || this == CRITICAL;
    }
    
    /**
     * Determine if this severity is critical
     * @return true for CRITICAL only
     */
    public boolean isCritical() {
        return this == CRITICAL;
    }
}
