package com.farm.equipment.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for updating an existing connected sensor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSensorRequest implements Serializable {
    
    @Min(value = 0, message = "Battery level must be between 0 and 100")
    @Max(value = 100, message = "Battery level must be between 0 and 100")
    private Integer battery;
    
    private String location;
    private String model;
    private Boolean active;
    
    @Min(value = 0, message = "Alert threshold must be between 0 and 100")
    @Max(value = 100, message = "Alert threshold must be between 0 and 100")
    private Integer alertThreshold;
}
