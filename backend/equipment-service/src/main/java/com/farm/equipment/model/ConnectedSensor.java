package com.farm.equipment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a connected sensor equipment.
 */
@Entity
@Table(name = "connected_sensors", indexes = {
    @Index(name = "idx_sensor_farm_id", columnList = "farm_id"),
    @Index(name = "idx_sensor_type", columnList = "type"),
    @Index(name = "idx_sensor_battery", columnList = "battery")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
public class ConnectedSensor extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @NotNull(message = "Sensor type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private SensorType type;
    
    @NotNull(message = "Farm ID is required")
    @Column(name = "farm_id", nullable = false)
    private UUID farmId;
    
    @NotNull(message = "Battery level is required")
    @Min(value = 0, message = "Battery level must be between 0 and 100")
    @Max(value = 100, message = "Battery level must be between 0 and 100")
    @Column(name = "battery", nullable = false)
    private Integer battery;
    
    @Column(name = "last_communication")
    private LocalDateTime lastCommunication;
    
    @Column(name = "location", length = 255)
    private String location;
    
    @Column(name = "model", length = 100)
    private String model;
    
    @Column(name = "installation_date")
    private LocalDateTime installationDate;
    
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    
    @Column(name = "alert_threshold")
    private Integer alertThreshold = 20;
    
    @Column(name = "last_maintenance_date")
    private LocalDateTime lastMaintenanceDate;
    
    @Column(name = "next_maintenance_date")
    private LocalDateTime nextMaintenanceDate;
    
    /**
     * Check if battery is low
     * @return true if battery is below alert threshold
     */
    public boolean isBatteryLow() {
        return battery != null && alertThreshold != null && battery <= alertThreshold;
    }
    
    /**
     * Check if battery is critical (below 10%)
     * @return true if battery is below 10%
     */
    public boolean isBatteryCritical() {
        return battery != null && battery <= 10;
    }
    
    /**
     * Check if sensor is online (communicated within last hour)
     * @return true if last communication was within last hour
     */
    public boolean isOnline() {
        return lastCommunication != null && 
               lastCommunication.isAfter(LocalDateTime.now().minusHours(1));
    }
    
    /**
     * Update communication timestamp
     */
    public void updateCommunication() {
        this.lastCommunication = LocalDateTime.now();
    }
    
    /**
     * Update battery level
     * @param newBatteryLevel new battery percentage
     */
    public void updateBattery(int newBatteryLevel) {
        if (newBatteryLevel >= 0 && newBatteryLevel <= 100) {
            this.battery = newBatteryLevel;
            updateCommunication();
        }
    }
    
    /**
     * Get battery status description
     * @return status description
     */
    public String getBatteryStatus() {
        if (isBatteryCritical()) {
            return "CRITICAL";
        } else if (isBatteryLow()) {
            return "LOW";
        } else if (battery >= 80) {
            return "GOOD";
        } else {
            return "NORMAL";
        }
    }
    
    /**
     * Get measurement unit for this sensor
     * @return unit string
     */
    public String getUnit() {
        return type != null ? type.getUnit() : "";
    }
}
