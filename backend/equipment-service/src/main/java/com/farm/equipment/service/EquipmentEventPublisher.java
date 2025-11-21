package com.farm.equipment.service;

import com.farm.equipment.event.EquipmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for publishing equipment events to RabbitMQ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${rabbitmq.exchange.name:equipment-events}")
    private String exchangeName;
    
    /**
     * Publish an equipment event to RabbitMQ
     * @param event the event to publish
     */
    public void publishEvent(EquipmentEvent event) {
        try {
            String routingKey = "equipment." + event.getEventType().name().toLowerCase();
            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
            log.info("Published event: {} for equipment: {}", event.getEventType(), event.getEquipmentId());
        } catch (Exception e) {
            log.error("Failed to publish event: {}", event.getEventType(), e);
            // Don't throw exception - event publishing failure shouldn't break the operation
        }
    }
    
    /**
     * Publish equipment created event
     */
    public void publishEquipmentCreated(EquipmentEvent event) {
        publishEvent(event);
    }
    
    /**
     * Publish status changed event
     */
    public void publishStatusChanged(EquipmentEvent event) {
        publishEvent(event);
    }
    
    /**
     * Publish maintenance scheduled event
     */
    public void publishMaintenanceScheduled(EquipmentEvent event) {
        publishEvent(event);
    }
    
    /**
     * Publish maintenance completed event
     */
    public void publishMaintenanceCompleted(EquipmentEvent event) {
        publishEvent(event);
    }
    
    /**
     * Publish battery low event
     */
    public void publishBatteryLow(EquipmentEvent event) {
        publishEvent(event);
    }
    
    /**
     * Publish sensor offline event
     */
    public void publishSensorOffline(EquipmentEvent event) {
        publishEvent(event);
    }
}
