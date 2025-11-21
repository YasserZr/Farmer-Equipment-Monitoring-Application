package com.farm.equipment.repository;

import com.farm.equipment.model.ConnectedPump;
import com.farm.equipment.model.EquipmentStatus;
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
 * Repository interface for ConnectedPump entity.
 */
@Repository
public interface ConnectedPumpRepository extends JpaRepository<ConnectedPump, UUID> {
    
    /**
     * Find all pumps for a specific farm
     */
    List<ConnectedPump> findByFarmId(UUID farmId);
    
    /**
     * Find all pumps for a specific farm (paginated)
     */
    Page<ConnectedPump> findByFarmId(UUID farmId, Pageable pageable);
    
    /**
     * Find pumps by status
     */
    List<ConnectedPump> findByStatus(EquipmentStatus status);
    
    /**
     * Find pumps by status (paginated)
     */
    Page<ConnectedPump> findByStatus(EquipmentStatus status, Pageable pageable);
    
    /**
     * Find pumps by farm and status
     */
    List<ConnectedPump> findByFarmIdAndStatus(UUID farmId, EquipmentStatus status);
    
    /**
     * Find operational pumps for a farm
     */
    @Query("SELECT p FROM ConnectedPump p WHERE p.farmId = :farmId AND p.status = 'ACTIVE'")
    List<ConnectedPump> findOperationalPumpsByFarmId(@Param("farmId") UUID farmId);
    
    /**
     * Find pumps with overdue maintenance
     */
    @Query("SELECT p FROM ConnectedPump p WHERE p.nextMaintenanceDate < :currentDate AND p.status != 'MAINTENANCE'")
    List<ConnectedPump> findPumpsWithOverdueMaintenance(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find pumps with overdue maintenance (paginated)
     */
    @Query("SELECT p FROM ConnectedPump p WHERE p.nextMaintenanceDate < :currentDate AND p.status != 'MAINTENANCE'")
    Page<ConnectedPump> findPumpsWithOverdueMaintenance(@Param("currentDate") LocalDateTime currentDate, Pageable pageable);
    
    /**
     * Find pumps needing maintenance soon (within specified days)
     */
    @Query("SELECT p FROM ConnectedPump p WHERE p.nextMaintenanceDate BETWEEN :startDate AND :endDate")
    List<ConnectedPump> findPumpsNeedingMaintenanceSoon(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Search pumps by model
     */
    List<ConnectedPump> findByModelContainingIgnoreCase(String model);
    
    /**
     * Count pumps by farm
     */
    long countByFarmId(UUID farmId);
    
    /**
     * Count pumps by status
     */
    long countByStatus(EquipmentStatus status);
    
    /**
     * Count operational pumps by farm
     */
    @Query("SELECT COUNT(p) FROM ConnectedPump p WHERE p.farmId = :farmId AND p.status = 'ACTIVE'")
    long countOperationalPumpsByFarmId(@Param("farmId") UUID farmId);
    
    /**
     * Get pump statistics by farm
     */
    @Query("SELECT p.status, COUNT(p) FROM ConnectedPump p WHERE p.farmId = :farmId GROUP BY p.status")
    List<Object[]> getPumpStatisticsByFarm(@Param("farmId") UUID farmId);
}
