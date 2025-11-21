package com.farm.supervision.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO for dashboard statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatisticsDTO implements Serializable {
    
    private long totalEvents;
    private long unacknowledgedEvents;
    private long criticalEvents;
    private long warningEvents;
    private long infoEvents;
    
    private Map<String, Long> eventCountsByType;
    private Map<String, Long> eventCountsBySeverity;
    
    private List<EventDTO> recentCriticalEvents;
    private List<EventDTO> recentEvents;
    
    private Map<String, Long> dailyEventCounts;
    
    private long eventsLast24Hours;
    private long eventsLast7Days;
    private long eventsLast30Days;
}
