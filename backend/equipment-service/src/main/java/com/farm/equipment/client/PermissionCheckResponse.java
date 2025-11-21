package com.farm.equipment.client;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for permission check response from Farmers service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionCheckResponse implements Serializable {
    
    private UUID farmerId;
    private UUID resourceId;
    private String action;
    private boolean allowed;
    private String reason;
    private String farmerRole;
}
