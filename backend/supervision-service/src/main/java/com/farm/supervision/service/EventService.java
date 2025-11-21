package com.farm.supervision.service;

import com.farm.supervision.dto.AcknowledgeEventRequest;
import com.farm.supervision.dto.EventDTO;
import com.farm.supervision.dto.EventFilterRequest;
import com.farm.supervision.model.EquipmentEvent;
import com.farm.supervision.repository.EquipmentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing equipment events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventService {
    
    private final EquipmentEventRepository eventRepository;
    
    /**
     * Get all events with filtering and pagination
     */
    public Page<EventDTO> getAllEvents(EventFilterRequest filter, Pageable pageable) {
        log.debug("Getting events with filter: {}", filter);
        
        Page<EquipmentEvent> events = eventRepository.findByFilters(
                filter.getFarmId(),
                filter.getEquipmentId(),
                filter.getEventType(),
                filter.getSeverity(),
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getAcknowledged(),
                pageable
        );
        
        return events.map(this::convertToDTO);
    }
    
    /**
     * Get event by ID
     */
    public EventDTO getEventById(UUID eventId) {
        log.debug("Getting event with ID: {}", eventId);
        
        EquipmentEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        
        return convertToDTO(event);
    }
    
    /**
     * Get unacknowledged critical events
     */
    public Page<EventDTO> getUnacknowledgedCriticalEvents(Pageable pageable) {
        log.debug("Getting unacknowledged critical events");
        
        return eventRepository.findUnacknowledgedCriticalEvents(pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Get events by farm ID
     */
    public Page<EventDTO> getEventsByFarmId(UUID farmId, Pageable pageable) {
        log.debug("Getting events for farm: {}", farmId);
        
        return eventRepository.findByFarmId(farmId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Get events by equipment ID
     */
    public Page<EventDTO> getEventsByEquipmentId(UUID equipmentId, Pageable pageable) {
        log.debug("Getting events for equipment: {}", equipmentId);
        
        return eventRepository.findByEquipmentId(equipmentId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Acknowledge an event
     */
    @Transactional
    public EventDTO acknowledgeEvent(UUID eventId, AcknowledgeEventRequest request) {
        log.info("Acknowledging event {} by {}", eventId, request.getAcknowledgedBy());
        
        EquipmentEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        
        event.acknowledge(request.getAcknowledgedBy());
        
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            event.markProcessed(request.getNotes());
        }
        
        EquipmentEvent savedEvent = eventRepository.save(event);
        
        return convertToDTO(savedEvent);
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
    
    /**
     * Custom exception for event not found
     */
    public static class EventNotFoundException extends RuntimeException {
        public EventNotFoundException(UUID eventId) {
            super("Event not found with ID: " + eventId);
        }
    }
}
