package com.farm.equipment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for maintenance schedule item.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceScheduleDTO implements Serializable {
    
    private UUID equipmentId;
    private String equipmentType; // PUMP or SENSOR
    private String equipmentName;
    private UUID farmId;
    private LocalDateTime scheduledDate;
    private LocalDateTime lastMaintenanceDate;
    private String status; // SCHEDULED, OVERDUE, COMPLETED
    private String notes;
}
