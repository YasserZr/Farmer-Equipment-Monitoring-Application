package com.farm.equipment.service;

import com.farm.equipment.dto.response.EquipmentStatisticsDTO;
import com.farm.equipment.model.EquipmentStatus;
import com.farm.equipment.repository.ConnectedPumpRepository;
import com.farm.equipment.repository.ConnectedSensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for equipment statistics operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsService {
    
    private final ConnectedPumpRepository pumpRepository;
    private final ConnectedSensorRepository sensorRepository;
    
    /**
     * Get overall equipment statistics.
     */
    public EquipmentStatisticsDTO getEquipmentStatistics() {
        log.debug("Calculating equipment statistics");
        
        // Pump statistics
        long totalPumps = pumpRepository.count();
        long activePumps = pumpRepository.countByStatus(EquipmentStatus.ACTIVE);
        long inactivePumps = pumpRepository.countByStatus(EquipmentStatus.INACTIVE);
        long maintenancePumps = pumpRepository.countByStatus(EquipmentStatus.MAINTENANCE);
        
        // Sensor statistics
        long totalSensors = sensorRepository.count();
        long activeSensors = sensorRepository.countByActive(true);
        long inactiveSensors = sensorRepository.countByActive(false);
        long lowBatterySensors = sensorRepository.findByBatteryBetween(20, 50).size();
        long criticalBatterySensors = sensorRepository.countByBatteryLessThan(20);
        
        // Overall statistics
        long totalEquipment = totalPumps + totalSensors;
        long activeEquipment = activePumps + activeSensors;
        
        return EquipmentStatisticsDTO.builder()
                .totalPumps(totalPumps)
                .activePumps(activePumps)
                .inactivePumps(inactivePumps)
                .maintenancePumps(maintenancePumps)
                .totalSensors(totalSensors)
                .activeSensors(activeSensors)
                .inactiveSensors(inactiveSensors)
                .lowBatterySensors(lowBatterySensors)
                .criticalBatterySensors(criticalBatterySensors)
                .totalEquipment(totalEquipment)
                .activeEquipment(activeEquipment)
                .build();
    }
}
