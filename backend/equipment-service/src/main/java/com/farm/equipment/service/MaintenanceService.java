package com.farm.equipment.service;

import com.farm.equipment.dto.response.MaintenanceScheduleDTO;
import com.farm.equipment.model.ConnectedPump;
import com.farm.equipment.model.ConnectedSensor;
import com.farm.equipment.repository.ConnectedPumpRepository;
import com.farm.equipment.repository.ConnectedSensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for managing maintenance schedules.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MaintenanceService {
    
    private final ConnectedPumpRepository pumpRepository;
    private final ConnectedSensorRepository sensorRepository;
    
    /**
     * Get all maintenance schedules
     */
    public List<MaintenanceScheduleDTO> getAllMaintenanceSchedules(Pageable pageable) {
        log.debug("Getting all maintenance schedules - page: {}, size: {}", 
                  pageable.getPageNumber(), pageable.getPageSize());
        
        // Get pumps with maintenance scheduled
        List<MaintenanceScheduleDTO> pumpSchedules = pumpRepository.findAll()
                .stream()
                .filter(pump -> pump.getNextMaintenanceDate() != null)
                .map(this::convertPumpToMaintenanceDTO)
                .collect(Collectors.toList());
        
        // Get sensors with maintenance scheduled
        List<MaintenanceScheduleDTO> sensorSchedules = sensorRepository.findAll()
                .stream()
                .filter(sensor -> sensor.getNextMaintenanceDate() != null)
                .map(this::convertSensorToMaintenanceDTO)
                .collect(Collectors.toList());
        
        // Combine and sort by scheduled date
        return Stream.concat(pumpSchedules.stream(), sensorSchedules.stream())
                .sorted(Comparator.comparing(MaintenanceScheduleDTO::getScheduledDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Get upcoming maintenance (within next 30 days)
     */
    public List<MaintenanceScheduleDTO> getUpcomingMaintenance() {
        log.debug("Getting upcoming maintenance");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);
        
        // Get pumps with upcoming maintenance
        List<MaintenanceScheduleDTO> pumpSchedules = pumpRepository.findAll()
                .stream()
                .filter(pump -> pump.getNextMaintenanceDate() != null &&
                               pump.getNextMaintenanceDate().isAfter(now) &&
                               pump.getNextMaintenanceDate().isBefore(thirtyDaysFromNow))
                .map(this::convertPumpToMaintenanceDTO)
                .collect(Collectors.toList());
        
        // Get sensors with upcoming maintenance
        List<MaintenanceScheduleDTO> sensorSchedules = sensorRepository.findAll()
                .stream()
                .filter(sensor -> sensor.getNextMaintenanceDate() != null &&
                                 sensor.getNextMaintenanceDate().isAfter(now) &&
                                 sensor.getNextMaintenanceDate().isBefore(thirtyDaysFromNow))
                .map(this::convertSensorToMaintenanceDTO)
                .collect(Collectors.toList());
        
        // Combine and sort by scheduled date
        return Stream.concat(pumpSchedules.stream(), sensorSchedules.stream())
                .sorted(Comparator.comparing(MaintenanceScheduleDTO::getScheduledDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Get overdue maintenance
     */
    public List<MaintenanceScheduleDTO> getOverdueMaintenance() {
        log.debug("Getting overdue maintenance");
        
        LocalDateTime now = LocalDateTime.now();
        
        // Get pumps with overdue maintenance
        List<MaintenanceScheduleDTO> pumpSchedules = pumpRepository.findAll()
                .stream()
                .filter(pump -> pump.getNextMaintenanceDate() != null &&
                               pump.getNextMaintenanceDate().isBefore(now))
                .map(pump -> {
                    MaintenanceScheduleDTO dto = convertPumpToMaintenanceDTO(pump);
                    dto.setStatus("OVERDUE");
                    return dto;
                })
                .collect(Collectors.toList());
        
        // Get sensors with overdue maintenance
        List<MaintenanceScheduleDTO> sensorSchedules = sensorRepository.findAll()
                .stream()
                .filter(sensor -> sensor.getNextMaintenanceDate() != null &&
                                 sensor.getNextMaintenanceDate().isBefore(now))
                .map(sensor -> {
                    MaintenanceScheduleDTO dto = convertSensorToMaintenanceDTO(sensor);
                    dto.setStatus("OVERDUE");
                    return dto;
                })
                .collect(Collectors.toList());
        
        // Combine and sort by scheduled date
        return Stream.concat(pumpSchedules.stream(), sensorSchedules.stream())
                .sorted(Comparator.comparing(MaintenanceScheduleDTO::getScheduledDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Convert pump to maintenance DTO
     */
    private MaintenanceScheduleDTO convertPumpToMaintenanceDTO(ConnectedPump pump) {
        LocalDateTime now = LocalDateTime.now();
        String status;
        
        if (pump.getNextMaintenanceDate().isBefore(now)) {
            status = "OVERDUE";
        } else if (pump.getNextMaintenanceDate().isBefore(now.plusDays(7))) {
            status = "URGENT";
        } else {
            status = "SCHEDULED";
        }
        
        return MaintenanceScheduleDTO.builder()
                .equipmentId(pump.getId())
                .equipmentType("PUMP")
                .equipmentName(pump.getModel() + " - " + pump.getLocation())
                .farmId(pump.getFarmId())
                .scheduledDate(pump.getNextMaintenanceDate())
                .lastMaintenanceDate(pump.getLastMaintenanceDate())
                .status(status)
                .notes("Regular maintenance scheduled")
                .build();
    }
    
    /**
     * Convert sensor to maintenance DTO
     */
    private MaintenanceScheduleDTO convertSensorToMaintenanceDTO(ConnectedSensor sensor) {
        LocalDateTime now = LocalDateTime.now();
        String status;
        
        if (sensor.getNextMaintenanceDate().isBefore(now)) {
            status = "OVERDUE";
        } else if (sensor.getNextMaintenanceDate().isBefore(now.plusDays(7))) {
            status = "URGENT";
        } else {
            status = "SCHEDULED";
        }
        
        return MaintenanceScheduleDTO.builder()
                .equipmentId(sensor.getId())
                .equipmentType("SENSOR")
                .equipmentName(sensor.getType() + " - " + sensor.getLocation())
                .farmId(sensor.getFarmId())
                .scheduledDate(sensor.getNextMaintenanceDate())
                .lastMaintenanceDate(sensor.getLastMaintenanceDate())
                .status(status)
                .notes("Regular sensor maintenance scheduled")
                .build();
    }
}
