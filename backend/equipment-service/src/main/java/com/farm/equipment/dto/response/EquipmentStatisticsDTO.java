package com.farm.equipment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for equipment statistics response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentStatisticsDTO {
    
    private Long totalPumps;
    private Long activePumps;
    private Long inactivePumps;
    private Long maintenancePumps;
    
    private Long totalSensors;
    private Long activeSensors;
    private Long inactiveSensors;
    private Long lowBatterySensors;
    private Long criticalBatterySensors;
    
    private Long totalEquipment;
    private Long activeEquipment;
}
