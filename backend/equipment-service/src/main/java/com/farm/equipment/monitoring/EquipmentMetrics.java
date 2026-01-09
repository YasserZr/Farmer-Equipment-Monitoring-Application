package com.farm.equipment.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Custom metrics for Equipment Service
 * Tracks business-specific metrics for monitoring and alerting
 */
@Component
@RequiredArgsConstructor
public class EquipmentMetrics {

    private final MeterRegistry meterRegistry;

    /**
     * Track equipment status changes
     * Usage: equipmentMetrics.recordStatusChange("PUMP", "OPERATIONAL", "FAULTY");
     */
    public void recordStatusChange(String equipmentType, String oldStatus, String newStatus) {
        Counter.builder("equipment.status.changes")
                .description("Number of equipment status changes")
                .tag("type", equipmentType)
                .tag("from", oldStatus)
                .tag("to", newStatus)
                .register(meterRegistry)
                .increment();
    }

    /**
     * Track equipment operation duration
     * Usage: Timer.Sample sample = equipmentMetrics.startTimer();
     *        // ... perform operation ...
     *        equipmentMetrics.recordOperationTime(sample, "PUMP", "maintenance");
     */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordOperationTime(Timer.Sample sample, String equipmentType, String operation) {
        sample.stop(Timer.builder("equipment.operation.time")
                .description("Time taken for equipment operations")
                .tag("type", equipmentType)
                .tag("operation", operation)
                .register(meterRegistry));
    }

    /**
     * Track maintenance requests
     */
    public void recordMaintenanceRequest(String equipmentType, String priority) {
        Counter.builder("equipment.maintenance.requests")
                .description("Number of maintenance requests")
                .tag("type", equipmentType)
                .tag("priority", priority)
                .register(meterRegistry)
                .increment();
    }

    /**
     * Track equipment failures
     */
    public void recordEquipmentFailure(String equipmentType, String failureReason) {
        Counter.builder("equipment.failures")
                .description("Number of equipment failures")
                .tag("type", equipmentType)
                .tag("reason", failureReason)
                .register(meterRegistry)
                .increment();
    }

    /**
     * Track sensor readings anomalies
     */
    public void recordSensorAnomaly(String sensorType, String anomalyType) {
        Counter.builder("sensor.anomalies")
                .description("Number of sensor reading anomalies")
                .tag("sensor_type", sensorType)
                .tag("anomaly_type", anomalyType)
                .register(meterRegistry)
                .increment();
    }

    /**
     * Track battery levels (for battery-powered equipment)
     */
    public void recordBatteryLevel(String equipmentId, double batteryLevel) {
        meterRegistry.gauge("equipment.battery.level",
                java.util.List.of(
                        io.micrometer.core.instrument.Tag.of("equipment_id", equipmentId)
                ),
                batteryLevel);
    }

    /**
     * Track RabbitMQ message publishing
     */
    public void recordMessagePublished(String messageType, boolean success) {
        Counter.builder("equipment.messages.published")
                .description("Number of messages published to RabbitMQ")
                .tag("type", messageType)
                .tag("success", String.valueOf(success))
                .register(meterRegistry)
                .increment();
    }

    /**
     * Track API calls to Farmers Service
     */
    public void recordFarmersServiceCall(String endpoint, String status, long durationMs) {
        Counter.builder("equipment.farmers_service.calls")
                .description("Number of calls to Farmers Service")
                .tag("endpoint", endpoint)
                .tag("status", status)
                .register(meterRegistry)
                .increment();

        meterRegistry.timer("equipment.farmers_service.call.duration",
                        "endpoint", endpoint,
                        "status", status)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }
}
