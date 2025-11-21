package com.farm.equipment.exception;

import java.util.UUID;

/**
 * Exception thrown when a sensor is not found.
 */
public class SensorNotFoundException extends RuntimeException {
    
    public SensorNotFoundException(UUID sensorId) {
        super(String.format("Sensor not found with ID: %s", sensorId));
    }
}
