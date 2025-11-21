package com.farm.equipment.model;

/**
 * Enum representing sensor types.
 */
public enum SensorType {
    TEMPERATURE,
    HUMIDITY,
    SOIL_MOISTURE;
    
    /**
     * Get the measurement unit for this sensor type
     * @return unit of measurement
     */
    public String getUnit() {
        return switch (this) {
            case TEMPERATURE -> "°C";
            case HUMIDITY -> "%";
            case SOIL_MOISTURE -> "%";
        };
    }
    
    /**
     * Check if sensor measures environmental conditions
     * @return true for TEMPERATURE and HUMIDITY
     */
    public boolean isEnvironmentalSensor() {
        return this == TEMPERATURE || this == HUMIDITY;
    }
}
