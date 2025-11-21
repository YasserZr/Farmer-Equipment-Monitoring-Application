package com.farm.equipment.dto.request;

import com.farm.equipment.model.SensorType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for creating a new connected sensor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSensorRequest implements Serializable {
    
    @NotNull(message = "Sensor type is required")
    private SensorType type;
    
    @NotNull(message = "Farm ID is required")
    private UUID farmId;
    
    @NotNull(message = "Battery level is required")
    @Min(value = 0, message = "Battery level must be between 0 and 100")
    @Max(value = 100, message = "Battery level must be between 0 and 100")
    private Integer battery;
    
    private String location;
    private String model;
    private LocalDateTime installationDate;
    
    @Min(value = 0, message = "Alert threshold must be between 0 and 100")
    @Max(value = 100, message = "Alert threshold must be between 0 and 100")
    private Integer alertThreshold;
}
