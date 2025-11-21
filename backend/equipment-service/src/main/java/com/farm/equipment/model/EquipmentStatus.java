package com.farm.equipment.model;

/**
 * Enum representing equipment status.
 */
public enum EquipmentStatus {
    ACTIVE,
    INACTIVE,
    MAINTENANCE;
    
    /**
     * Check if equipment is operational
     * @return true if status is ACTIVE
     */
    public boolean isOperational() {
        return this == ACTIVE;
    }
    
    /**
     * Check if equipment needs attention
     * @return true if status is MAINTENANCE
     */
    public boolean needsMaintenance() {
        return this == MAINTENANCE;
    }
}
