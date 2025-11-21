package com.farm.supervision.dto;

import com.farm.supervision.model.EventSeverity;
import com.farm.supervision.model.EventType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for filtering events.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFilterRequest {
    
    private UUID farmId;
    private UUID equipmentId;
    private EventType eventType;
    private EventSeverity severity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean acknowledged;
}
