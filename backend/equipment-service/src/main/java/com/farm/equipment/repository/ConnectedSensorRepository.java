package com.farm.equipment.repository;

import com.farm.equipment.model.ConnectedSensor;
import com.farm.equipment.model.SensorType;
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
 * Repository interface for ConnectedSensor entity.
 */
@Repository
public interface ConnectedSensorRepository extends JpaRepository<ConnectedSensor, UUID> {
    
    /**
     * Find all sensors for a specific farm
     */
    List<ConnectedSensor> findByFarmId(UUID farmId);
    
    /**
     * Find all sensors for a specific farm (paginated)
     */
    Page<ConnectedSensor> findByFarmId(UUID farmId, Pageable pageable);
    
    /**
     * Find sensors by type
     */
    List<ConnectedSensor> findByType(SensorType type);
    
    /**
     * Find sensors by type (paginated)
     */
    Page<ConnectedSensor> findByType(SensorType type, Pageable pageable);
    
    /**
     * Find sensors by farm and type
     */
    List<ConnectedSensor> findByFarmIdAndType(UUID farmId, SensorType type);
    
    /**
     * Find active sensors for a farm
     */
    List<ConnectedSensor> findByFarmIdAndActiveTrue(UUID farmId);
    
    /**
     * Find sensors with low battery
     */
    @Query("SELECT s FROM ConnectedSensor s WHERE s.battery <= s.alertThreshold")
    List<ConnectedSensor> findSensorsWithLowBattery();
    
    /**
     * Find sensors with low battery (paginated)
     */
    @Query("SELECT s FROM ConnectedSensor s WHERE s.battery <= s.alertThreshold")
    Page<ConnectedSensor> findSensorsWithLowBattery(Pageable pageable);
    
    /**
     * Find sensors with critical battery (below 10%)
     */
    @Query("SELECT s FROM ConnectedSensor s WHERE s.battery <= 10")
    List<ConnectedSensor> findSensorsWithCriticalBattery();
    
    /**
     * Find offline sensors (no communication for specified duration)
     */
    @Query("SELECT s FROM ConnectedSensor s WHERE s.lastCommunication < :threshold OR s.lastCommunication IS NULL")
    List<ConnectedSensor> findOfflineSensors(@Param("threshold") LocalDateTime threshold);
    
    /**
     * Find offline sensors (paginated)
     */
    @Query("SELECT s FROM ConnectedSensor s WHERE s.lastCommunication < :threshold OR s.lastCommunication IS NULL")
    Page<ConnectedSensor> findOfflineSensors(@Param("threshold") LocalDateTime threshold, Pageable pageable);
    
    /**
     * Find sensors by battery range
     */
    List<ConnectedSensor> findByBatteryBetween(Integer minBattery, Integer maxBattery);
    
    /**
     * Search sensors by model
     */
    List<ConnectedSensor> findByModelContainingIgnoreCase(String model);
    
    /**
     * Count sensors by farm
     */
    long countByFarmId(UUID farmId);
    
    /**
     * Count sensors by type
     */
    long countByType(SensorType type);
    
    /**
     * Count active sensors by farm
     */
    long countByFarmIdAndActiveTrue(UUID farmId);
    
    /**
     * Get sensor statistics by farm
     */
    @Query("SELECT s.type, COUNT(s), AVG(s.battery) FROM ConnectedSensor s WHERE s.farmId = :farmId GROUP BY s.type")
    List<Object[]> getSensorStatisticsByFarm(@Param("farmId") UUID farmId);
    
    /**
     * Get average battery level by farm
     */
    @Query("SELECT AVG(s.battery) FROM ConnectedSensor s WHERE s.farmId = :farmId")
    Double getAverageBatteryByFarm(@Param("farmId") UUID farmId);
}
