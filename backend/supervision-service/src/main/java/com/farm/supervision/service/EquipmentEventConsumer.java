package com.farm.supervision.service;

import com.farm.supervision.dto.EventDTO;
import com.farm.supervision.model.EquipmentEvent;
import com.farm.supervision.model.EventSeverity;
import com.farm.supervision.model.EventType;
import com.farm.supervision.repository.EquipmentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Service for consuming equipment events from RabbitMQ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentEventConsumer {
    
    private final EquipmentEventRepository eventRepository;
    private final AlertService alertService;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Listen to equipment events from RabbitMQ
     * @param eventMessage the incoming event message
     */
    @RabbitListener(queues = "${rabbitmq.queue.supervision:supervision-events-queue}")
    @Transactional
    public void consumeEvent(Map<String, Object> eventMessage) {
        try {
            log.info("Received equipment event: {}", eventMessage);
            
            // Parse the event message
            EquipmentEvent event = parseEventMessage(eventMessage);
            
            // Save to database
            EquipmentEvent savedEvent = eventRepository.save(event);
            log.info("Saved event with ID: {}", savedEvent.getId());
            
            // Check if alert is needed
            if (event.getSeverity().requiresAttention()) {
                alertService.processAlert(savedEvent);
            }
            
            // Broadcast event via WebSocket
            broadcastEvent(savedEvent);
            
        } catch (Exception e) {
            log.error("Error processing event: {}", eventMessage, e);
            throw e; // Re-throw to trigger DLQ routing
        }
    }
    
    /**
     * Parse event message from RabbitMQ into EquipmentEvent entity
     */
    private EquipmentEvent parseEventMessage(Map<String, Object> message) {
        String eventTypeStr = (String) message.get("eventType");
        EventType eventType = EventType.valueOf(eventTypeStr);
        
        UUID equipmentId = UUID.fromString((String) message.get("equipmentId"));
        UUID farmId = UUID.fromString((String) message.get("farmId"));
        String equipmentType = (String) message.get("equipmentType");
        String messageText = (String) message.get("message");
        
        // Parse timestamp
        LocalDateTime timestamp = LocalDateTime.now();
        if (message.containsKey("timestamp")) {
            try {
                timestamp = LocalDateTime.parse((String) message.get("timestamp"));
            } catch (Exception e) {
                log.warn("Failed to parse timestamp, using current time");
            }
        }
        
        // Get metadata
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) message.get("metadata");
        
        // Determine severity
        EventSeverity severity = EquipmentEvent.determineSeverity(eventType);
        
        return EquipmentEvent.builder()
                .eventType(eventType)
                .equipmentId(equipmentId)
                .equipmentType(equipmentType)
                .farmId(farmId)
                .timestamp(timestamp)
                .payload(metadata)
                .message(messageText)
                .severity(severity)
                .receivedAt(LocalDateTime.now())
                .acknowledged(false)
                .processed(false)
                .build();
    }
    
    /**
     * Broadcast event to WebSocket subscribers
     */
    private void broadcastEvent(EquipmentEvent event) {
        try {
            EventDTO eventDTO = convertToDTO(event);
            
            // Broadcast to all subscribers
            messagingTemplate.convertAndSend("/topic/events", eventDTO);
            
            // Broadcast to farm-specific topic
            messagingTemplate.convertAndSend("/topic/events/farm/" + event.getFarmId(), eventDTO);
            
            // Broadcast critical events to alert topic
            if (event.getSeverity() == EventSeverity.CRITICAL) {
                messagingTemplate.convertAndSend("/topic/alerts", eventDTO);
            }
            
            log.debug("Broadcasted event {} via WebSocket", event.getId());
        } catch (Exception e) {
            log.error("Error broadcasting event via WebSocket", e);
            // Don't fail the entire operation if WebSocket broadcast fails
        }
    }
    
    /**
     * Convert entity to DTO
     */
    private EventDTO convertToDTO(EquipmentEvent event) {
        return EventDTO.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .equipmentId(event.getEquipmentId())
                .equipmentType(event.getEquipmentType())
                .farmId(event.getFarmId())
                .timestamp(event.getTimestamp())
                .payload(event.getPayload())
                .message(event.getMessage())
                .severity(event.getSeverity())
                .acknowledged(event.isAcknowledged())
                .acknowledgedAt(event.getAcknowledgedAt())
                .acknowledgedBy(event.getAcknowledgedBy())
                .receivedAt(event.getReceivedAt())
                .processed(event.isProcessed())
                .processingNotes(event.getProcessingNotes())
                .build();
    }
}
