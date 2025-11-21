package com.farm.supervision.service;

import com.farm.supervision.model.EquipmentEvent;
import com.farm.supervision.model.EventSeverity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for processing and generating alerts from critical events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {
    
    /**
     * Process an alert for a critical or warning event
     * @param event the event requiring attention
     */
    public void processAlert(EquipmentEvent event) {
        log.warn("Processing alert for event: {} - {} (Severity: {})", 
                 event.getId(), event.getEventType(), event.getSeverity());
        
        switch (event.getEventType()) {
            case BATTERY_LOW -> handleBatteryLowAlert(event);
            case SENSOR_OFFLINE -> handleSensorOfflineAlert(event);
            case EQUIPMENT_FAILURE -> handleEquipmentFailureAlert(event);
            case STATUS_CHANGED -> handleStatusChangeAlert(event);
            case MAINTENANCE_SCHEDULED -> handleMaintenanceAlert(event);
            default -> log.info("No specific alert action for event type: {}", event.getEventType());
        }
    }
    
    /**
     * Handle battery low alert
     */
    private void handleBatteryLowAlert(EquipmentEvent event) {
        Integer batteryLevel = extractBatteryLevel(event);
        log.error("CRITICAL: Low battery detected for equipment {} on farm {}. Battery level: {}%",
                  event.getEquipmentId(), event.getFarmId(), batteryLevel);
        
        // In production, this would:
        // - Send email/SMS notifications
        // - Create a ticket in ticketing system
        // - Escalate if battery is critically low (<10%)
    }
    
    /**
     * Handle sensor offline alert
     */
    private void handleSensorOfflineAlert(EquipmentEvent event) {
        log.error("CRITICAL: Sensor {} is offline on farm {}. Last communication: {}",
                  event.getEquipmentId(), event.getFarmId(), 
                  event.getPayload().get("lastCommunication"));
        
        // In production:
        // - Send immediate notification
        // - Check if this is part of a pattern (multiple sensors offline)
        // - Schedule automatic retry/check
    }
    
    /**
     * Handle equipment failure alert
     */
    private void handleEquipmentFailureAlert(EquipmentEvent event) {
        log.error("CRITICAL: Equipment failure detected for {} on farm {}",
                  event.getEquipmentId(), event.getFarmId());
        
        // In production:
        // - Immediate escalation to on-call team
        // - Check for backup equipment
        // - Initiate emergency procedures
    }
    
    /**
     * Handle status change alert
     */
    private void handleStatusChangeAlert(EquipmentEvent event) {
        String oldStatus = (String) event.getPayload().getOrDefault("oldStatus", "UNKNOWN");
        String newStatus = (String) event.getPayload().getOrDefault("newStatus", "UNKNOWN");
        
        log.warn("Equipment {} status changed from {} to {} on farm {}",
                 event.getEquipmentId(), oldStatus, newStatus, event.getFarmId());
        
        // In production:
        // - Log status change history
        // - Check if unauthorized status change
        // - Update equipment availability status
    }
    
    /**
     * Handle maintenance alert
     */
    private void handleMaintenanceAlert(EquipmentEvent event) {
        log.info("Maintenance scheduled for equipment {} on farm {}",
                 event.getEquipmentId(), event.getFarmId());
        
        // In production:
        // - Send reminder notifications
        // - Update maintenance calendar
        // - Assign maintenance team
    }
    
    /**
     * Extract battery level from event payload
     */
    private Integer extractBatteryLevel(EquipmentEvent event) {
        Object batteryObj = event.getPayload().get("batteryLevel");
        if (batteryObj instanceof Integer) {
            return (Integer) batteryObj;
        } else if (batteryObj instanceof String) {
            return Integer.parseInt((String) batteryObj);
        }
        return null;
    }
    
    /**
     * Determine if alert should be escalated
     */
    public boolean shouldEscalate(EquipmentEvent event) {
        if (event.getSeverity() != EventSeverity.CRITICAL) {
            return false;
        }
        
        // Escalate if not acknowledged within 30 minutes
        if (!event.isAcknowledged() && 
            event.getReceivedAt().plusMinutes(30).isBefore(java.time.LocalDateTime.now())) {
            return true;
        }
        
        // Escalate battery critical events immediately
        if (event.getEventType() == com.farm.supervision.model.EventType.BATTERY_LOW) {
            Integer batteryLevel = extractBatteryLevel(event);
            return batteryLevel != null && batteryLevel <= 10;
        }
        
        return false;
    }
}
