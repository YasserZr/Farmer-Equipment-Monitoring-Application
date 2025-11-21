package com.farm.equipment.dto.response;

import com.farm.equipment.model.EquipmentStatus;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for connected pump response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PumpDTO implements Serializable {
    
    private UUID id;
    private UUID farmId;
    private String model;
    private EquipmentStatus status;
    private BigDecimal maxFlow;
    private String formattedMaxFlow;
    private String location;
    private LocalDateTime installationDate;
    private LocalDateTime lastMaintenanceDate;
    private LocalDateTime nextMaintenanceDate;
    private String maintenanceNotes;
    private boolean operational;
    private boolean maintenanceOverdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
