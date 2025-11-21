package com.farm.farmers.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for creating a new farm.
 * Contains all required fields for farm registration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFarmRequest implements Serializable {
    
    @NotNull(message = "Farmer ID is required")
    private UUID farmerId;
    
    @NotBlank(message = "Farm name is required")
    @Size(min = 2, max = 150, message = "Farm name must be between 2 and 150 characters")
    private String name;
    
    @NotBlank(message = "Location is required")
    @Size(min = 3, max = 255, message = "Location must be between 3 and 255 characters")
    private String location;
    
    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.01", message = "Area must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Area must be less than 1,000,000")
    @Digits(integer = 6, fraction = 2, message = "Area must have at most 6 digits before decimal and 2 after")
    private BigDecimal area;
}
