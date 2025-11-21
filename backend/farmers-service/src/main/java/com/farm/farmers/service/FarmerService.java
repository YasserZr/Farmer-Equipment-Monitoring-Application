package com.farm.farmers.service;

import com.farm.farmers.dto.request.CreateFarmerRequest;
import com.farm.farmers.dto.request.UpdateFarmerRequest;
import com.farm.farmers.dto.response.FarmerDTO;
import com.farm.farmers.dto.response.PermissionCheckResponse;
import com.farm.farmers.model.FarmerRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for Farmer operations.
 * Defines business logic methods for farmer management.
 */
public interface FarmerService {
    
    /**
     * Create a new farmer
     * @param request the farmer creation request
     * @return the created farmer DTO
     */
    FarmerDTO createFarmer(CreateFarmerRequest request);
    
    /**
     * Get farmer by ID
     * @param id the farmer ID
     * @return the farmer DTO
     */
    FarmerDTO getFarmerById(UUID id);
    
    /**
     * Get farmer by email
     * @param email the farmer email
     * @return the farmer DTO
     */
    FarmerDTO getFarmerByEmail(String email);
    
    /**
     * Get all farmers with pagination
     * @param pageable pagination parameters
     * @return page of farmer DTOs
     */
    Page<FarmerDTO> getAllFarmers(Pageable pageable);
    
    /**
     * Search farmers by name
     * @param name search term
     * @param pageable pagination parameters
     * @return page of matching farmer DTOs
     */
    Page<FarmerDTO> searchFarmersByName(String name, Pageable pageable);
    
    /**
     * Get farmers by role
     * @param role the farmer role
     * @param pageable pagination parameters
     * @return page of farmer DTOs with the specified role
     */
    Page<FarmerDTO> getFarmersByRole(FarmerRole role, Pageable pageable);
    
    /**
     * Get farmers with admin privileges
     * @param pageable pagination parameters
     * @return page of admin farmers
     */
    Page<FarmerDTO> getFarmersWithAdminPrivileges(Pageable pageable);
    
    /**
     * Get recently registered farmers
     * @param days number of days to look back
     * @param pageable pagination parameters
     * @return page of recently registered farmers
     */
    Page<FarmerDTO> getRecentlyRegisteredFarmers(int days, Pageable pageable);
    
    /**
     * Update farmer information
     * @param id the farmer ID
     * @param request the update request
     * @return the updated farmer DTO
     */
    FarmerDTO updateFarmer(UUID id, UpdateFarmerRequest request);
    
    /**
     * Delete a farmer
     * @param id the farmer ID
     */
    void deleteFarmer(UUID id);
    
    /**
     * Check if farmer exists
     * @param id the farmer ID
     * @return true if exists
     */
    boolean farmerExists(UUID id);
    
    /**
     * Check if email is already registered
     * @param email the email to check
     * @return true if email exists
     */
    boolean emailExists(String email);
    
    /**
     * Check farmer permissions for a resource and action
     * @param farmerId the farmer ID
     * @param resourceId the resource ID (e.g., pump ID)
     * @param action the action to perform (e.g., "start", "stop")
     * @return permission check response
     */
    PermissionCheckResponse checkPermission(UUID farmerId, UUID resourceId, String action);
    
    /**
     * Get total farmer count
     * @return total number of farmers
     */
    long getTotalFarmerCount();
    
    /**
     * Get farmer count by role
     * @param role the farmer role
     * @return count of farmers with the role
     */
    long getFarmerCountByRole(FarmerRole role);
}
