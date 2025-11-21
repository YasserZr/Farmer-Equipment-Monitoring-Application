package com.farm.farmers.dto.request;

import com.farm.farmers.model.FarmerRole;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for creating a new farmer.
 * Contains all required fields for farmer registration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFarmerRequest implements Serializable {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$",
             message = "Phone number format is invalid")
    private String phone;
    
    @NotNull(message = "Role is required")
    private FarmerRole role;
}
