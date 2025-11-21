package com.farm.farmers.dto.response;

import com.farm.farmers.model.FarmerRole;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for farmer response.
 * Contains all farmer information for API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerDTO implements Serializable {
    
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private FarmerRole role;
    private LocalDateTime registrationDate;
    private int farmCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
