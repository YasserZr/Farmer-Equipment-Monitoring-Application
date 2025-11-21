package com.farm.equipment.event;

/**
 * Enum representing equipment event types for messaging.
 */
public enum EquipmentEventType {
    EQUIPMENT_CREATED,
    STATUS_CHANGED,
    MAINTENANCE_SCHEDULED,
    MAINTENANCE_COMPLETED,
    BATTERY_LOW,
    SENSOR_OFFLINE
}
