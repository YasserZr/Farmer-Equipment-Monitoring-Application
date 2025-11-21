package com.farm.equipment.dto.request;

import com.farm.equipment.model.EquipmentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for updating an existing connected pump.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePumpRequest implements Serializable {
    
    @Size(min = 2, max = 100, message = "Model must be between 2 and 100 characters")
    private String model;
    
    private EquipmentStatus status;
    
    @DecimalMin(value = "0.01", message = "Max flow must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Max flow must be less than 100,000")
    @Digits(integer = 5, fraction = 2)
    private BigDecimal maxFlow;
    
    private String location;
}
