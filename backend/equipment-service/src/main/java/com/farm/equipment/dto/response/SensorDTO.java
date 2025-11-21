package com.farm.equipment.dto.response;

import com.farm.equipment.model.SensorType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for connected sensor response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorDTO implements Serializable {
    
    private UUID id;
    private SensorType type;
    private UUID farmId;
    private Integer battery;
    private String batteryStatus;
    private LocalDateTime lastCommunication;
    private String location;
    private String model;
    private LocalDateTime installationDate;
    private boolean active;
    private Integer alertThreshold;
    private boolean batteryLow;
    private boolean batteryCritical;
    private boolean online;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
