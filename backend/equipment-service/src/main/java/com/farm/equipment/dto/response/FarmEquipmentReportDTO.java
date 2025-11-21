package com.farm.equipment.dto.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for farm equipment report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmEquipmentReportDTO implements Serializable {
    
    private UUID farmId;
    private int totalPumps;
    private int activePumps;
    private int inactivePumps;
    private int pumpsInMaintenance;
    private int pumpsWithOverdueMaintenance;
    private int totalSensors;
    private int activeSensors;
    private int sensorsWithLowBattery;
    private int offlineSensors;
    private Double averageBatteryLevel;
    private List<PumpDTO> pumps;
    private List<SensorDTO> sensors;
}
