package com.farm.supervision.dto;

import com.farm.supervision.model.EventSeverity;
import com.farm.supervision.model.EventType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for equipment event response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO implements Serializable {
    
    private UUID id;
    private EventType eventType;
    private UUID equipmentId;
    private String equipmentType;
    private UUID farmId;
    private LocalDateTime timestamp;
    private Map<String, Object> payload;
    private String message;
    private EventSeverity severity;
    private boolean acknowledged;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    private LocalDateTime receivedAt;
    private boolean processed;
    private String processingNotes;
}
