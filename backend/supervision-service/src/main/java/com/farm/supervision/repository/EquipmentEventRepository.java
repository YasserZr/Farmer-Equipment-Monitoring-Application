package com.farm.supervision.repository;

import com.farm.supervision.model.EquipmentEvent;
import com.farm.supervision.model.EventSeverity;
import com.farm.supervision.model.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for EquipmentEvent entity.
 */
@Repository
public interface EquipmentEventRepository extends JpaRepository<EquipmentEvent, UUID> {
    
    /**
     * Find events by farm ID
     */
    Page<EquipmentEvent> findByFarmId(UUID farmId, Pageable pageable);
    
    /**
     * Find events by equipment ID
     */
    Page<EquipmentEvent> findByEquipmentId(UUID equipmentId, Pageable pageable);
    
    /**
     * Find events by event type
     */
    Page<EquipmentEvent> findByEventType(EventType eventType, Pageable pageable);
    
    /**
     * Find events by severity
     */
    Page<EquipmentEvent> findBySeverity(EventSeverity severity, Pageable pageable);
    
    /**
     * Find unacknowledged critical events
     */
    @Query("SELECT e FROM EquipmentEvent e WHERE e.acknowledged = false AND e.severity = 'CRITICAL' ORDER BY e.timestamp DESC")
    List<EquipmentEvent> findUnacknowledgedCriticalEvents();
    
    /**
     * Find unacknowledged events requiring attention
     */
    @Query("SELECT e FROM EquipmentEvent e WHERE e.acknowledged = false AND e.severity IN ('CRITICAL', 'WARNING') ORDER BY e.timestamp DESC")
    Page<EquipmentEvent> findUnacknowledgedAlerts(Pageable pageable);
    
    /**
     * Find events within date range
     */
    @Query("SELECT e FROM EquipmentEvent e WHERE e.timestamp BETWEEN :startDate AND :endDate ORDER BY e.timestamp DESC")
    Page<EquipmentEvent> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Find events by farm and date range
     */
    @Query("SELECT e FROM EquipmentEvent e WHERE e.farmId = :farmId AND e.timestamp BETWEEN :startDate AND :endDate ORDER BY e.timestamp DESC")
    Page<EquipmentEvent> findByFarmIdAndDateRange(
        @Param("farmId") UUID farmId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Advanced filtering with multiple criteria
     */
    @Query("SELECT e FROM EquipmentEvent e WHERE " +
           "(:farmId IS NULL OR e.farmId = :farmId) AND " +
           "(:equipmentId IS NULL OR e.equipmentId = :equipmentId) AND " +
           "(:eventType IS NULL OR e.eventType = :eventType) AND " +
           "(:severity IS NULL OR e.severity = :severity) AND " +
           "(:startDate IS NULL OR e.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR e.timestamp <= :endDate) AND " +
           "(:acknowledged IS NULL OR e.acknowledged = :acknowledged) " +
           "ORDER BY e.timestamp DESC")
    Page<EquipmentEvent> findByFilters(
        @Param("farmId") UUID farmId,
        @Param("equipmentId") UUID equipmentId,
        @Param("eventType") EventType eventType,
        @Param("severity") EventSeverity severity,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("acknowledged") Boolean acknowledged,
        Pageable pageable
    );
    
    /**
     * Count events by severity
     */
    long countBySeverity(EventSeverity severity);
    
    /**
     * Count unacknowledged events
     */
    long countByAcknowledgedFalse();
    
    /**
     * Count events by farm and severity
     */
    long countByFarmIdAndSeverity(UUID farmId, EventSeverity severity);
    
    /**
     * Get event counts by type
     */
    @Query("SELECT e.eventType, COUNT(e) FROM EquipmentEvent e GROUP BY e.eventType")
    List<Object[]> getEventCountsByType();
    
    /**
     * Get event counts by severity
     */
    @Query("SELECT e.severity, COUNT(e) FROM EquipmentEvent e GROUP BY e.severity")
    List<Object[]> getEventCountsBySeverity();
    
    /**
     * Get recent events
     */
    @Query("SELECT e FROM EquipmentEvent e WHERE e.timestamp >= :since ORDER BY e.timestamp DESC")
    List<EquipmentEvent> findRecentEvents(@Param("since") LocalDateTime since, Pageable pageable);
    
    /**
     * Get daily event counts for the last N days
     */
    @Query("SELECT CAST(e.timestamp AS date), COUNT(e) FROM EquipmentEvent e " +
           "WHERE e.timestamp >= :startDate " +
           "GROUP BY CAST(e.timestamp AS date) " +
           "ORDER BY CAST(e.timestamp AS date) DESC")
    List<Object[]> getDailyEventCounts(@Param("startDate") LocalDateTime startDate);
}
