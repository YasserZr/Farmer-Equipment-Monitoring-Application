package com.farm.supervision.dto;

import lombok.*;

/**
 * DTO for acknowledging events.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcknowledgeEventRequest {
    
    private String acknowledgedBy;
    private String notes;
}
