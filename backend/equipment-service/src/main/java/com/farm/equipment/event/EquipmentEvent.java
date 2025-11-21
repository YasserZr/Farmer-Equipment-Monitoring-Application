package com.farm.equipment.event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Event DTO for equipment-related events published to RabbitMQ.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentEvent implements Serializable {
    
    private UUID eventId;
    private EquipmentEventType eventType;
    private UUID equipmentId;
    private String equipmentType; // "PUMP" or "SENSOR"
    private UUID farmId;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    private String message;
    
    /**
     * Create equipment created event
     */
    public static EquipmentEvent createEquipmentCreated(UUID equipmentId, String equipmentType, UUID farmId, Map<String, Object> metadata) {
        return EquipmentEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(EquipmentEventType.EQUIPMENT_CREATED)
                .equipmentId(equipmentId)
                .equipmentType(equipmentType)
                .farmId(farmId)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .message(String.format("%s equipment created for farm %s", equipmentType, farmId))
                .build();
    }
    
    /**
     * Create status changed event
     */
    public static EquipmentEvent createStatusChanged(UUID equipmentId, String equipmentType, UUID farmId, String oldStatus, String newStatus) {
        return EquipmentEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(EquipmentEventType.STATUS_CHANGED)
                .equipmentId(equipmentId)
                .equipmentType(equipmentType)
                .farmId(farmId)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of("oldStatus", oldStatus, "newStatus", newStatus))
                .message(String.format("%s status changed from %s to %s", equipmentType, oldStatus, newStatus))
                .build();
    }
    
    /**
     * Create maintenance scheduled event
     */
    public static EquipmentEvent createMaintenanceScheduled(UUID equipmentId, UUID farmId, LocalDateTime scheduledDate) {
        return EquipmentEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(EquipmentEventType.MAINTENANCE_SCHEDULED)
                .equipmentId(equipmentId)
                .equipmentType("PUMP")
                .farmId(farmId)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of("scheduledDate", scheduledDate.toString()))
                .message(String.format("Maintenance scheduled for %s", scheduledDate))
                .build();
    }
    
    /**
     * Create maintenance completed event
     */
    public static EquipmentEvent createMaintenanceCompleted(UUID equipmentId, UUID farmId, String notes) {
        return EquipmentEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(EquipmentEventType.MAINTENANCE_COMPLETED)
                .equipmentId(equipmentId)
                .equipmentType("PUMP")
                .farmId(farmId)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of("notes", notes != null ? notes : ""))
                .message("Maintenance completed successfully")
                .build();
    }
    
    /**
     * Create battery low event
     */
    public static EquipmentEvent createBatteryLow(UUID sensorId, UUID farmId, int batteryLevel) {
        return EquipmentEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(EquipmentEventType.BATTERY_LOW)
                .equipmentId(sensorId)
                .equipmentType("SENSOR")
                .farmId(farmId)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of("batteryLevel", batteryLevel))
                .message(String.format("Sensor battery low: %d%%", batteryLevel))
                .build();
    }
    
    /**
     * Create sensor offline event
     */
    public static EquipmentEvent createSensorOffline(UUID sensorId, UUID farmId, LocalDateTime lastCommunication) {
        return EquipmentEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(EquipmentEventType.SENSOR_OFFLINE)
                .equipmentId(sensorId)
                .equipmentType("SENSOR")
                .farmId(farmId)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of("lastCommunication", lastCommunication != null ? lastCommunication.toString() : "never"))
                .message("Sensor is offline")
                .build();
    }
}
