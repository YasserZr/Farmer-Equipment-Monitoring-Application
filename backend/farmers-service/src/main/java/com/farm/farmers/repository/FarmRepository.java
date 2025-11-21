package com.farm.farmers.repository;

import com.farm.farmers.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Farm entity.
 * Provides CRUD operations and custom queries for farm data access.
 */
@Repository
public interface FarmRepository extends JpaRepository<Farm, UUID> {
    
    /**
     * Find all farms belonging to a specific farmer
     * @param farmerId the farmer's UUID
     * @return list of farms owned by the farmer
     */
    List<Farm> findByFarmerId(UUID farmerId);
    
    /**
     * Find farms by name (case-insensitive partial match)
     * @param name the farm name search term
     * @return list of matching farms
     */
    List<Farm> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find farms by location (case-insensitive partial match)
     * @param location the location search term
     * @return list of matching farms
     */
    List<Farm> findByLocationContainingIgnoreCase(String location);
    
    /**
     * Find farms with area greater than or equal to specified value
     * @param minArea minimum area
     * @return list of farms meeting the criteria
     */
    List<Farm> findByAreaGreaterThanEqual(BigDecimal minArea);
    
    /**
     * Find farms with area less than or equal to specified value
     * @param maxArea maximum area
     * @return list of farms meeting the criteria
     */
    List<Farm> findByAreaLessThanEqual(BigDecimal maxArea);
    
    /**
     * Find farms within an area range
     * @param minArea minimum area
     * @param maxArea maximum area
     * @return list of farms within the area range
     */
    List<Farm> findByAreaBetween(BigDecimal minArea, BigDecimal maxArea);
    
    /**
     * Count farms belonging to a specific farmer
     * @param farmerId the farmer's UUID
     * @return count of farms
     */
    long countByFarmerId(UUID farmerId);
    
    /**
     * Check if a farm with the given name exists for a specific farmer
     * @param farmerId the farmer's UUID
     * @param name the farm name
     * @return true if farm exists
     */
    boolean existsByFarmerIdAndName(UUID farmerId, String name);
    
    /**
     * Find a specific farm by farmer ID and farm name
     * @param farmerId the farmer's UUID
     * @param name the farm name
     * @return Optional containing the farm if found
     */
    Optional<Farm> findByFarmerIdAndName(UUID farmerId, String name);
    
    /**
     * Find large farms (area > specified threshold)
     * @param areaThreshold the area threshold
     * @return list of large farms
     */
    @Query("SELECT f FROM Farm f WHERE f.area > :areaThreshold ORDER BY f.area DESC")
    List<Farm> findLargeFarms(@Param("areaThreshold") BigDecimal areaThreshold);
    
    /**
     * Get total area of all farms for a specific farmer
     * @param farmerId the farmer's UUID
     * @return total area or 0 if no farms
     */
    @Query("SELECT COALESCE(SUM(f.area), 0) FROM Farm f WHERE f.farmerId = :farmerId")
    BigDecimal getTotalAreaByFarmerId(@Param("farmerId") UUID farmerId);
    
    /**
     * Find farms with farmer details eagerly loaded
     * @param farmId the farm ID
     * @return Optional containing the farm with farmer
     */
    @Query("SELECT f FROM Farm f LEFT JOIN FETCH f.farmer WHERE f.id = :farmId")
    Optional<Farm> findByIdWithFarmer(@Param("farmId") UUID farmId);
    
    /**
     * Search farms by multiple criteria
     * @param farmerId optional farmer ID filter
     * @param name optional name search term
     * @param location optional location search term
     * @param minArea optional minimum area
     * @param maxArea optional maximum area
     * @return list of matching farms
     */
    @Query("SELECT f FROM Farm f WHERE " +
           "(:farmerId IS NULL OR f.farmerId = :farmerId) AND " +
           "(:name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:location IS NULL OR LOWER(f.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minArea IS NULL OR f.area >= :minArea) AND " +
           "(:maxArea IS NULL OR f.area <= :maxArea)")
    List<Farm> searchFarms(@Param("farmerId") UUID farmerId,
                          @Param("name") String name,
                          @Param("location") String location,
                          @Param("minArea") BigDecimal minArea,
                          @Param("maxArea") BigDecimal maxArea);
    
    /**
     * Get average farm area across all farms
     * @return average area or 0 if no farms
     */
    @Query("SELECT COALESCE(AVG(f.area), 0) FROM Farm f")
    BigDecimal getAverageFarmArea();
    
    /**
     * Find top N largest farms
     * @param limit number of farms to return
     * @return list of largest farms
     */
    @Query("SELECT f FROM Farm f ORDER BY f.area DESC")
    List<Farm> findTopLargestFarms(@Param("limit") int limit);
    
    /**
     * Count total number of farms in the system
     * @return total farm count
     */
    @Query("SELECT COUNT(f) FROM Farm f")
    long getTotalFarmCount();
}
