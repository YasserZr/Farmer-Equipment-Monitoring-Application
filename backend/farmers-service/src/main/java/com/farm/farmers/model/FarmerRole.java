package com.farm.farmers.model;

/**
 * Enum representing the role of a farmer in the system.
 * Defines the access level and responsibilities of different farmer types.
 */
public enum FarmerRole {
    /**
     * Owner has full control over the farm and can manage all aspects
     */
    OWNER("Farm Owner - Full access and control"),
    
    /**
     * Manager can manage daily operations and workers but has limited administrative rights
     */
    MANAGER("Farm Manager - Operational management"),
    
    /**
     * Worker has basic access to perform assigned tasks
     */
    WORKER("Farm Worker - Basic operational access");
    
    private final String description;
    
    FarmerRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this role has administrative privileges
     * @return true if role is OWNER or MANAGER
     */
    public boolean hasAdminPrivileges() {
        return this == OWNER || this == MANAGER;
    }
    
    /**
     * Check if this role can manage workers
     * @return true if role is OWNER or MANAGER
     */
    public boolean canManageWorkers() {
        return this == OWNER || this == MANAGER;
    }
    
    /**
     * Check if this role is an owner
     * @return true if role is OWNER
     */
    public boolean isOwner() {
        return this == OWNER;
    }
}
