package com.farm.supervision.service;

import com.farm.supervision.dto.DashboardStatisticsDTO;
import com.farm.supervision.dto.EventDTO;
import com.farm.supervision.model.EquipmentEvent;
import com.farm.supervision.model.EventSeverity;
import com.farm.supervision.repository.EquipmentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for generating dashboard statistics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsService {
    
    private final EquipmentEventRepository eventRepository;
    
    /**
     * Get comprehensive dashboard statistics
     */
    public DashboardStatisticsDTO getDashboardStatistics() {
        log.debug("Generating dashboard statistics");
        
        LocalDateTime now = LocalDateTime.now();
        
        return DashboardStatisticsDTO.builder()
                .totalEvents(eventRepository.count())
                .unacknowledgedEvents(eventRepository.countByAcknowledgedFalse())
                .criticalEvents(eventRepository.countBySeverity(EventSeverity.CRITICAL))
                .warningEvents(eventRepository.countBySeverity(EventSeverity.WARNING))
                .infoEvents(eventRepository.countBySeverity(EventSeverity.INFO))
                .eventCountsByType(getEventCountsByType())
                .eventCountsBySeverity(getEventCountsBySeverity())
                .recentCriticalEvents(getRecentCriticalEvents())
                .recentEvents(getRecentEvents(10))
                .dailyEventCounts(getDailyEventCounts(30))
                .eventsLast24Hours(countEventsInPeriod(now.minusHours(24), now))
                .eventsLast7Days(countEventsInPeriod(now.minusDays(7), now))
                .eventsLast30Days(countEventsInPeriod(now.minusDays(30), now))
                .build();
    }
    
    /**
     * Get event counts grouped by type
     */
    private Map<String, Long> getEventCountsByType() {
        List<Object[]> results = eventRepository.getEventCountsByType();
        Map<String, Long> counts = new HashMap<>();
        
        for (Object[] result : results) {
            String type = result[0].toString();
            Long count = ((Number) result[1]).longValue();
            counts.put(type, count);
        }
        
        return counts;
    }
    
    /**
     * Get event counts grouped by severity
     */
    private Map<String, Long> getEventCountsBySeverity() {
        List<Object[]> results = eventRepository.getEventCountsBySeverity();
        Map<String, Long> counts = new HashMap<>();
        
        for (Object[] result : results) {
            String severity = result[0].toString();
            Long count = ((Number) result[1]).longValue();
            counts.put(severity, count);
        }
        
        return counts;
    }
    
    /**
     * Get recent critical events (last 10)
     */
    private List<EventDTO> getRecentCriticalEvents() {
        return eventRepository.findBySeverity(EventSeverity.CRITICAL, PageRequest.of(0, 10))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get recent events
     */
    private List<EventDTO> getRecentEvents(int limit) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return eventRepository.findRecentEvents(since, PageRequest.of(0, limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get daily event counts for last N days
     */
    private Map<String, Long> getDailyEventCounts(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Object[]> results = eventRepository.getDailyEventCounts(startDate);
        
        Map<String, Long> counts = new HashMap<>();
        for (Object[] result : results) {
            String date = result[0].toString();
            Long count = ((Number) result[1]).longValue();
            counts.put(date, count);
        }
        
        return counts;
    }
    
    /**
     * Count events in a specific period
     */
    private long countEventsInPeriod(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByDateRange(start, end, PageRequest.of(0, 1))
                .getTotalElements();
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
