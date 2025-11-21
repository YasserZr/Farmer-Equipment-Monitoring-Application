package com.farm.supervision.model;

/**
 * Enum representing event types from equipment.
 */
public enum EventType {
    EQUIPMENT_CREATED,
    STATUS_CHANGED,
    MAINTENANCE_SCHEDULED,
    MAINTENANCE_COMPLETED,
    BATTERY_LOW,
    SENSOR_OFFLINE,
    EQUIPMENT_FAILURE,
    SYSTEM_ALERT
}
