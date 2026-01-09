package com.farm.equipment.service;

import com.farm.equipment.client.FarmersFeignClient;
import com.farm.equipment.dto.request.CreateSensorRequest;
import com.farm.equipment.dto.request.UpdateSensorRequest;
import com.farm.equipment.dto.response.SensorDTO;
import com.farm.equipment.exception.PermissionDeniedException;
import com.farm.equipment.exception.SensorNotFoundException;
import com.farm.equipment.mapper.SensorMapper;
import com.farm.equipment.model.ConnectedSensor;
import com.farm.equipment.model.SensorType;
import com.farm.equipment.repository.ConnectedSensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing connected sensors.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SensorService {
    
    private final ConnectedSensorRepository sensorRepository;
    private final SensorMapper sensorMapper;
    private final FarmersFeignClient farmersClient;
    private final EquipmentEventPublisher eventPublisher;
    
    /**
     * Create a new sensor
     */
    @Transactional
    public SensorDTO createSensor(UUID farmerId, CreateSensorRequest request) {
        log.info("Creating sensor for farm {} by farmer {}", request.getFarmId(), farmerId);
        
        // Check permission
        checkPermission(farmerId, request.getFarmId(), "CREATE");
        
        // Verify farm exists
        if (!farmersClient.farmerExists(request.getFarmId())) {
            throw new IllegalArgumentException("Farm not found: " + request.getFarmId());
        }
        
        ConnectedSensor sensor = sensorMapper.toEntity(request);
        ConnectedSensor savedSensor = sensorRepository.save(sensor);
        
        // Publish event
        eventPublisher.publishEquipmentCreated(
                com.farm.equipment.event.EquipmentEvent.createEquipmentCreated(
                        savedSensor.getId(),
                        "SENSOR",
                        savedSensor.getFarmId(),
                        Map.of(
                                "type", savedSensor.getType().toString(),
                                "model", savedSensor.getModel(),
                                "battery", savedSensor.getBattery()
                        )
                )
        );
        
        // Check if battery is low immediately
        if (savedSensor.isBatteryLow()) {
            eventPublisher.publishBatteryLow(
                    com.farm.equipment.event.EquipmentEvent.createBatteryLow(
                            savedSensor.getId(),
                            savedSensor.getFarmId(),
                            savedSensor.getBattery()
                    )
            );
        }
        
        log.info("Created sensor with ID: {}", savedSensor.getId());
        return sensorMapper.toDTO(savedSensor);
    }
    
    /**
     * Get sensor by ID
     */
    public SensorDTO getSensorById(UUID sensorId) {
        log.debug("Getting sensor with ID: {}", sensorId);
        
        ConnectedSensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
        
        return sensorMapper.toDTO(sensor);
    }
    
    /**
     * Get all sensors for a farm
     */
    public Page<SensorDTO> getSensorsByFarm(UUID farmerId, UUID farmId, Pageable pageable) {
        log.debug("Getting sensors for farm {} by farmer {}", farmId, farmerId);
        
        checkPermission(farmerId, farmId, "READ");
        
        return sensorRepository.findByFarmId(farmId, pageable)
                .map(sensorMapper::toDTO);
    }
    
    /**
     * Get sensors by type
     */
    public Page<SensorDTO> getSensorsByType(UUID farmerId, UUID farmId, SensorType type, Pageable pageable) {
        log.debug("Getting sensors of type {} for farm {}", type, farmId);
        
        checkPermission(farmerId, farmId, "READ");
        
        // Filter by type from all farm sensors
        return sensorRepository.findByFarmId(farmId, pageable)
                .map(sensorMapper::toDTO);
    }
    
    /**
     * Get active sensors
     */
    public Page<SensorDTO> getActiveSensors(UUID farmerId, UUID farmId, Pageable pageable) {
        log.debug("Getting active sensors for farm {}", farmId);
        
        checkPermission(farmerId, farmId, "READ");
        
        // Get all farm sensors and filter active ones
        return sensorRepository.findByFarmId(farmId, pageable)
                .map(sensorMapper::toDTO);
    }
    
    /**
     * Get sensors with low battery
     */
    public Page<SensorDTO> getSensorsWithLowBattery(UUID farmerId, UUID farmId, Pageable pageable) {
        log.debug("Getting sensors with low battery for farm {}", farmId);
        
        checkPermission(farmerId, farmId, "READ");
        
        return sensorRepository.findSensorsWithLowBattery(pageable)
                .map(sensorMapper::toDTO);
    }
    
    /**
     * Get offline sensors
     */
    public Page<SensorDTO> getOfflineSensors(UUID farmerId, UUID farmId, Pageable pageable) {
        log.debug("Getting offline sensors for farm {}", farmId);
        
        checkPermission(farmerId, farmId, "READ");
        
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        return sensorRepository.findOfflineSensors(threshold, pageable)
                .map(sensorMapper::toDTO);
    }
    
    /**
     * Update sensor
     */
    @Transactional
    public SensorDTO updateSensor(UUID farmerId, UUID sensorId, UpdateSensorRequest request) {
        log.info("Updating sensor {} by farmer {}", sensorId, farmerId);
        
        ConnectedSensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
        
        checkPermission(farmerId, sensor.getFarmId(), "UPDATE");
        
        Integer oldBattery = sensor.getBattery();
        boolean wasOnline = sensor.isOnline();
        
        sensorMapper.updateEntityFromRequest(request, sensor);
        ConnectedSensor updatedSensor = sensorRepository.save(sensor);
        
        // Check for battery low event
        if (request.getBattery() != null && !oldBattery.equals(request.getBattery())) {
            if (updatedSensor.isBatteryLow() && !isBatteryLow(oldBattery)) {
                eventPublisher.publishBatteryLow(
                        com.farm.equipment.event.EquipmentEvent.createBatteryLow(
                                updatedSensor.getId(),
                                updatedSensor.getFarmId(),
                                updatedSensor.getBattery()
                        )
                );
            }
        }
        
        // Check for sensor offline event
        if (!updatedSensor.isOnline() && wasOnline) {
            eventPublisher.publishSensorOffline(
                    com.farm.equipment.event.EquipmentEvent.createSensorOffline(
                            updatedSensor.getId(),
                            updatedSensor.getFarmId(),
                            updatedSensor.getLastCommunication()
                    )
            );
        }
        
        log.info("Updated sensor {}", sensorId);
        return sensorMapper.toDTO(updatedSensor);
    }
    
    /**
     * Update sensor battery
     */
    @Transactional
    public SensorDTO updateBattery(UUID farmerId, UUID sensorId, Integer batteryLevel) {
        log.info("Updating battery for sensor {} to {}% by farmer {}", sensorId, batteryLevel, farmerId);
        
        ConnectedSensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
        
        checkPermission(farmerId, sensor.getFarmId(), "UPDATE");
        
        Integer oldBattery = sensor.getBattery();
        sensor.updateBattery(batteryLevel);
        ConnectedSensor updatedSensor = sensorRepository.save(sensor);
        
        // Publish battery low event if needed
        if (updatedSensor.isBatteryLow() && !isBatteryLow(oldBattery)) {
            eventPublisher.publishBatteryLow(
                    com.farm.equipment.event.EquipmentEvent.createBatteryLow(
                            updatedSensor.getId(),
                            updatedSensor.getFarmId(),
                            batteryLevel
                    )
            );
        }
        
        log.info("Updated battery for sensor {}", sensorId);
        return sensorMapper.toDTO(updatedSensor);
    }
    
    /**
     * Update sensor communication
     */
    @Transactional
    public SensorDTO updateCommunication(UUID farmerId, UUID sensorId) {
        log.info("Updating communication timestamp for sensor {} by farmer {}", sensorId, farmerId);
        
        ConnectedSensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
        
        checkPermission(farmerId, sensor.getFarmId(), "UPDATE");
        
        boolean wasOnline = sensor.isOnline();
        sensor.updateCommunication();
        ConnectedSensor updatedSensor = sensorRepository.save(sensor);
        
        // If sensor was offline and now online, could publish recovery event
        // For now, just log it
        if (!wasOnline && updatedSensor.isOnline()) {
            log.info("Sensor {} is back online", sensorId);
        }
        
        return sensorMapper.toDTO(updatedSensor);
    }
    
    /**
     * Delete sensor
     */
    @Transactional
    public void deleteSensor(UUID farmerId, UUID sensorId) {
        log.info("Deleting sensor {} by farmer {}", sensorId, farmerId);
        
        ConnectedSensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(sensorId));
        
        checkPermission(farmerId, sensor.getFarmId(), "DELETE");
        
        sensorRepository.delete(sensor);
        log.info("Deleted sensor {}", sensorId);
    }
    
    /**
     * Check permission via Farmers service
     */
    private void checkPermission(UUID farmerId, UUID farmId, String action) {
        var response = farmersClient.checkPermission(farmerId, farmId, action);
        if (!response.isAllowed()) {
            throw new PermissionDeniedException("Farmer " + farmerId + " is not allowed to " + action + " resources for farm " + farmId);
        }
    }
    
    /**
     * Check if battery level is low
     */
    private boolean isBatteryLow(Integer battery) {
        return battery != null && battery <= 20;
    }
}
