package com.farm.farmers.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for updating an existing farm.
 * All fields are optional; only provided fields will be updated.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFarmRequest implements Serializable {
    
    @Size(min = 2, max = 150, message = "Farm name must be between 2 and 150 characters")
    private String name;
    
    @Size(min = 3, max = 255, message = "Location must be between 3 and 255 characters")
    private String location;
    
    @DecimalMin(value = "0.01", message = "Area must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Area must be less than 1,000,000")
    @Digits(integer = 6, fraction = 2, message = "Area must have at most 6 digits before decimal and 2 after")
    private BigDecimal area;
}
