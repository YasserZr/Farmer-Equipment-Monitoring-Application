package com.farm.farmers.service;

import com.farm.farmers.dto.request.CreateFarmRequest;
import com.farm.farmers.dto.request.UpdateFarmRequest;
import com.farm.farmers.dto.response.FarmDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service interface for Farm operations.
 * Defines business logic methods for farm management.
 */
public interface FarmService {
    
    /**
     * Create a new farm
     * @param request the farm creation request
     * @return the created farm DTO
     */
    FarmDTO createFarm(CreateFarmRequest request);
    
    /**
     * Get farm by ID
     * @param id the farm ID
     * @return the farm DTO
     */
    FarmDTO getFarmById(UUID id);
    
    /**
     * Get all farms with pagination
     * @param pageable pagination parameters
     * @return page of farm DTOs
     */
    Page<FarmDTO> getAllFarms(Pageable pageable);
    
    /**
     * Get farms by farmer ID
     * @param farmerId the farmer ID
     * @param pageable pagination parameters
     * @return page of farm DTOs
     */
    Page<FarmDTO> getFarmsByFarmerId(UUID farmerId, Pageable pageable);
    
    /**
     * Search farms by name or location
     * @param searchTerm search term
     * @param pageable pagination parameters
     * @return page of matching farm DTOs
     */
    Page<FarmDTO> searchFarms(String searchTerm, Pageable pageable);
    
    /**
     * Get farms by area range
     * @param minArea minimum area
     * @param maxArea maximum area
     * @param pageable pagination parameters
     * @return page of farm DTOs within the area range
     */
    Page<FarmDTO> getFarmsByAreaRange(BigDecimal minArea, BigDecimal maxArea, Pageable pageable);
    
    /**
     * Get large farms (area >= 100 hectares)
     * @param pageable pagination parameters
     * @return page of large farm DTOs
     */
    Page<FarmDTO> getLargeFarms(Pageable pageable);
    
    /**
     * Update farm information
     * @param id the farm ID
     * @param request the update request
     * @return the updated farm DTO
     */
    FarmDTO updateFarm(UUID id, UpdateFarmRequest request);
    
    /**
     * Delete a farm
     * @param id the farm ID
     */
    void deleteFarm(UUID id);
    
    /**
     * Check if farm exists
     * @param id the farm ID
     * @return true if exists
     */
    boolean farmExists(UUID id);
    
    /**
     * Get total area for a farmer
     * @param farmerId the farmer ID
     * @return total area in BigDecimal
     */
    BigDecimal getTotalAreaByFarmerId(UUID farmerId);
    
    /**
     * Get average farm area
     * @return average area in BigDecimal
     */
    BigDecimal getAverageFarmArea();
    
    /**
     * Get total farm count
     * @return total number of farms
     */
    long getTotalFarmCount();
    
    /**
     * Get farm count by farmer ID
     * @param farmerId the farmer ID
     * @return count of farms for the farmer
     */
    long getFarmCountByFarmerId(UUID farmerId);
}
