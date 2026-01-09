package com.farm.supervision.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom metrics for Supervision Service
 * Tracks events, alerts, and monitoring statistics
 */
@Component
@RequiredArgsConstructor
public class SupervisionMetrics {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeAlertsCount = new AtomicInteger(0);
    private final AtomicInteger unacknowledgedEventsCount = new AtomicInteger(0);

    public SupervisionMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Register gauges
        Gauge.builder("supervision.active_alerts", activeAlertsCount, AtomicInteger::get)
                .description("Number of active alerts")
                .register(meterRegistry);

        Gauge.builder("supervision.unacknowledged_events", unacknowledgedEventsCount, AtomicInteger::get)
                .description("Number of unacknowledged events")
                .register(meterRegistry);
    }

    /**
     * Track event creation
     */
    public void recordEventCreated(String eventType, String severity) {
        Counter.builder("supervision.events.created")
                .description("Number of events created")
                .tag("type", eventType)
                .tag("severity", severity)
                .register(meterRegistry)
                .increment();

        if ("CRITICAL".equals(severity) || "EMERGENCY".equals(severity)) {
            activeAlertsCount.incrementAndGet();
        }
        
        unacknowledgedEventsCount.incrementAndGet();
    }

    /**
     * Track event acknowledgment
     */
    public void recordEventAcknowledged(String eventType, String severity) {
        Counter.builder("supervision.events.acknowledged")
                .description("Number of events acknowledged")
                .tag("type", eventType)
                .tag("severity", severity)
                .register(meterRegistry)
                .increment();

        unacknowledgedEventsCount.decrementAndGet();
        
        if ("CRITICAL".equals(severity) || "EMERGENCY".equals(severity)) {
            activeAlertsCount.decrementAndGet();
        }
    }

    /**
     * Track WebSocket connections
     */
    public void recordWebSocketConnection(boolean connected) {
        Counter.builder("supervision.websocket.connections")
                .description("Number of WebSocket connections")
                .tag("connected", String.valueOf(connected))
                .register(meterRegistry)
                .increment();
    }

    /**
     * Track event notifications sent
     */
    public void recordNotificationSent(String channel, boolean success) {
        Counter.builder("supervision.notifications.sent")
                .description("Number of notifications sent")
                .tag("channel", channel)
                .tag("success", String.valueOf(success))
                .register(meterRegistry)
                .increment();
    }

    /**
     * Track RabbitMQ message consumption
     */
    public void recordMessageConsumed(String messageType, boolean success) {
        Counter.builder("supervision.messages.consumed")
                .description("Number of messages consumed from RabbitMQ")
                .tag("type", messageType)
                .tag("success", String.valueOf(success))
                .register(meterRegistry)
                .increment();
    }

    /**
     * Track event query performance
     */
    public void recordEventQuery(String filterType, int resultCount, long durationMs) {
        Counter.builder("supervision.event_queries")
                .description("Number of event queries")
                .tag("filter", filterType)
                .register(meterRegistry)
                .increment();

        meterRegistry.summary("supervision.event_query.results",
                        "filter", filterType)
                .record(resultCount);
    }

    /**
     * Update active alerts count
     */
    public void updateActiveAlertsCount(int count) {
        activeAlertsCount.set(count);
    }

    /**
     * Update unacknowledged events count
     */
    public void updateUnacknowledgedEventsCount(int count) {
        unacknowledgedEventsCount.set(count);
    }
}
