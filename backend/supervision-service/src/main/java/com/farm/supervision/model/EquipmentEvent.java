package com.farm.supervision.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entity representing an equipment event received from RabbitMQ.
 */
@Entity
@Table(name = "equipment_events", indexes = {
    @Index(name = "idx_event_farm_id", columnList = "farm_id"),
    @Index(name = "idx_event_equipment_id", columnList = "equipment_id"),
    @Index(name = "idx_event_type", columnList = "event_type"),
    @Index(name = "idx_event_timestamp", columnList = "timestamp"),
    @Index(name = "idx_event_severity", columnList = "severity")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class EquipmentEvent {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;
    
    @Column(name = "equipment_id", nullable = false)
    private UUID equipmentId;
    
    @Column(name = "equipment_type", length = 50)
    private String equipmentType;
    
    @Column(name = "farm_id", nullable = false)
    private UUID farmId;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private Map<String, Object> payload;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private EventSeverity severity;
    
    @Column(name = "acknowledged", nullable = false)
    private boolean acknowledged = false;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
    
    @Column(name = "acknowledged_by")
    private String acknowledgedBy;
    
    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;
    
    @Column(name = "processed", nullable = false)
    private boolean processed = false;
    
    @Column(name = "processing_notes", columnDefinition = "TEXT")
    private String processingNotes;
    
    /**
     * Determine severity based on event type
     * @param eventType the event type
     * @return appropriate severity
     */
    public static EventSeverity determineSeverity(EventType eventType) {
        return switch (eventType) {
            case BATTERY_LOW, SENSOR_OFFLINE, EQUIPMENT_FAILURE -> EventSeverity.CRITICAL;
            case STATUS_CHANGED, MAINTENANCE_SCHEDULED -> EventSeverity.WARNING;
            case EQUIPMENT_CREATED, MAINTENANCE_COMPLETED -> EventSeverity.INFO;
            default -> EventSeverity.INFO;
        };
    }
    
    /**
     * Acknowledge this event
     * @param acknowledgedBy user who acknowledged
     */
    public void acknowledge(String acknowledgedBy) {
        this.acknowledged = true;
        this.acknowledgedAt = LocalDateTime.now();
        this.acknowledgedBy = acknowledgedBy;
    }
    
    /**
     * Mark event as processed
     * @param notes processing notes
     */
    public void markProcessed(String notes) {
        this.processed = true;
        this.processingNotes = notes;
    }
}
