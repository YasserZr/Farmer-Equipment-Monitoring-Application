package com.farm.supervision.controller;

import com.farm.supervision.dto.AcknowledgeEventRequest;
import com.farm.supervision.dto.EventDTO;
import com.farm.supervision.dto.EventFilterRequest;
import com.farm.supervision.model.EventSeverity;
import com.farm.supervision.model.EventType;
import com.farm.supervision.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST Controller for managing equipment events.
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Events", description = "Equipment event management APIs")
public class EventController {
    
    private final EventService eventService;
    
    @Operation(summary = "Get all events", description = "Retrieve all equipment events with optional filtering and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<EventDTO>> getAllEvents(
            @Parameter(description = "Filter by farm ID") @RequestParam(required = false) UUID farmId,
            @Parameter(description = "Filter by equipment ID") @RequestParam(required = false) UUID equipmentId,
            @Parameter(description = "Filter by event type") @RequestParam(required = false) EventType eventType,
            @Parameter(description = "Filter by severity") @RequestParam(required = false) EventSeverity severity,
            @Parameter(description = "Start date") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Filter by acknowledgment status") @RequestParam(required = false) Boolean acknowledged,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/events - farmId: {}, equipmentId: {}, eventType: {}, severity: {}", 
                 farmId, equipmentId, eventType, severity);
        
        EventFilterRequest filter = EventFilterRequest.builder()
                .farmId(farmId)
                .equipmentId(equipmentId)
                .eventType(eventType)
                .severity(severity)
                .startDate(startDate)
                .endDate(endDate)
                .acknowledged(acknowledged)
                .build();
        
        Page<EventDTO> events = eventService.getAllEvents(filter, pageable);
        return ResponseEntity.ok(events);
    }
    
    @Operation(summary = "Get event by ID", description = "Retrieve a specific event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(
            @Parameter(description = "Event ID", required = true) @PathVariable UUID id) {
        
        log.info("GET /api/events/{}", id);
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    
    @Operation(summary = "Get unacknowledged critical events", 
               description = "Retrieve all unacknowledged critical events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/unacknowledged")
    public ResponseEntity<Page<EventDTO>> getUnacknowledgedCriticalEvents(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/events/unacknowledged");
        Page<EventDTO> events = eventService.getUnacknowledgedCriticalEvents(pageable);
        return ResponseEntity.ok(events);
    }
    
    @Operation(summary = "Get events by farm", description = "Retrieve all events for a specific farm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<Page<EventDTO>> getEventsByFarmId(
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/events/farm/{}", farmId);
        Page<EventDTO> events = eventService.getEventsByFarmId(farmId, pageable);
        return ResponseEntity.ok(events);
    }
    
    @Operation(summary = "Get events by equipment", description = "Retrieve all events for a specific equipment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<Page<EventDTO>> getEventsByEquipmentId(
            @Parameter(description = "Equipment ID", required = true) @PathVariable UUID equipmentId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/events/equipment/{}", equipmentId);
        Page<EventDTO> events = eventService.getEventsByEquipmentId(equipmentId, pageable);
        return ResponseEntity.ok(events);
    }
    
    @Operation(summary = "Acknowledge event", description = "Mark an event as acknowledged")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event acknowledged successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<EventDTO> acknowledgeEvent(
            @Parameter(description = "Event ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody AcknowledgeEventRequest request) {
        
        log.info("POST /api/events/{}/acknowledge - by: {}", id, request.getAcknowledgedBy());
        EventDTO event = eventService.acknowledgeEvent(id, request);
        return ResponseEntity.ok(event);
    }
}
