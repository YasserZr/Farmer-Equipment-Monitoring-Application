package com.farm.equipment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for scheduling or completing maintenance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRequest implements Serializable {
    
    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;
    
    @NotBlank(message = "Notes are required")
    private String notes;
}
