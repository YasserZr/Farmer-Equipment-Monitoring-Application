package com.farm.farmers.service;

import com.farm.farmers.dto.request.CreateFarmerRequest;
import com.farm.farmers.dto.request.UpdateFarmerRequest;
import com.farm.farmers.dto.response.FarmerDTO;
import com.farm.farmers.dto.response.PermissionCheckResponse;
import com.farm.farmers.exception.FarmerNotFoundException;
import com.farm.farmers.mapper.FarmerMapper;
import com.farm.farmers.model.Farmer;
import com.farm.farmers.model.FarmerRole;
import com.farm.farmers.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of FarmerService.
 * Provides business logic for farmer management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FarmerServiceImpl implements FarmerService {
    
    private final FarmerRepository farmerRepository;
    private final FarmerMapper farmerMapper;
    
    @Override
    @Transactional
    public FarmerDTO createFarmer(CreateFarmerRequest request) {
        log.info("Creating new farmer with email: {}", request.getEmail());
        
        // Check if email already exists
        if (farmerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        
        Farmer farmer = farmerMapper.toEntity(request);
        Farmer savedFarmer = farmerRepository.save(farmer);
        
        log.info("Successfully created farmer with ID: {}", savedFarmer.getId());
        return farmerMapper.toDTO(savedFarmer);
    }
    
    @Override
    public FarmerDTO getFarmerById(UUID id) {
        log.debug("Fetching farmer by ID: {}", id);
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException(id));
        return farmerMapper.toDTO(farmer);
    }
    
    @Override
    public FarmerDTO getFarmerByEmail(String email) {
        log.debug("Fetching farmer by email: {}", email);
        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new FarmerNotFoundException(email));
        return farmerMapper.toDTO(farmer);
    }
    
    @Override
    public Page<FarmerDTO> getAllFarmers(Pageable pageable) {
        log.debug("Fetching all farmers with pagination: {}", pageable);
        return farmerRepository.findAll(pageable)
                .map(farmerMapper::toDTO);
    }
    
    @Override
    public Page<FarmerDTO> searchFarmersByName(String name, Pageable pageable) {
        log.debug("Searching farmers by name: {}", name);
        return farmerRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(farmerMapper::toDTO);
    }
    
    @Override
    public Page<FarmerDTO> getFarmersByRole(FarmerRole role, Pageable pageable) {
        log.debug("Fetching farmers by role: {}", role);
        return farmerRepository.findByRole(role, pageable)
                .map(farmerMapper::toDTO);
    }
    
    @Override
    public Page<FarmerDTO> getFarmersWithAdminPrivileges(Pageable pageable) {
        log.debug("Fetching farmers with admin privileges");
        return farmerRepository.findFarmersWithAdminPrivileges(pageable)
                .map(farmerMapper::toDTO);
    }
    
    @Override
    public Page<FarmerDTO> getRecentlyRegisteredFarmers(int days, Pageable pageable) {
        log.debug("Fetching farmers registered in last {} days", days);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return farmerRepository.findRecentlyRegisteredFarmers(cutoffDate, pageable)
                .map(farmerMapper::toDTO);
    }
    
    @Override
    @Transactional
    public FarmerDTO updateFarmer(UUID id, UpdateFarmerRequest request) {
        log.info("Updating farmer with ID: {}", id);
        
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException(id));
        
        // Check email uniqueness if email is being updated
        if (request.getEmail() != null && !request.getEmail().equals(farmer.getEmail())) {
            if (farmerRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already registered: " + request.getEmail());
            }
        }
        
        farmerMapper.updateEntityFromRequest(request, farmer);
        Farmer updatedFarmer = farmerRepository.save(farmer);
        
        log.info("Successfully updated farmer with ID: {}", id);
        return farmerMapper.toDTO(updatedFarmer);
    }
    
    @Override
    @Transactional
    public void deleteFarmer(UUID id) {
        log.info("Deleting farmer with ID: {}", id);
        
        if (!farmerRepository.existsById(id)) {
            throw new FarmerNotFoundException(id);
        }
        
        farmerRepository.deleteById(id);
        log.info("Successfully deleted farmer with ID: {}", id);
    }
    
    @Override
    public boolean farmerExists(UUID id) {
        return farmerRepository.existsById(id);
    }
    
    @Override
    public boolean emailExists(String email) {
        return farmerRepository.existsByEmail(email);
    }
    
    @Override
    public PermissionCheckResponse checkPermission(UUID farmerId, UUID resourceId, String action) {
        log.debug("Checking permission for farmer: {}, resource: {}, action: {}", 
                  farmerId, resourceId, action);
        
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new FarmerNotFoundException(farmerId));
        
        // Business logic for permission checking
        boolean allowed = false;
        String reason = "";
        
        // OWNER has full permissions
        if (farmer.isOwner()) {
            allowed = true;
            reason = "Farmer is an owner with full permissions";
        }
        // MANAGER can manage most operations except critical administrative tasks
        else if (farmer.getRole() == FarmerRole.MANAGER) {
            if ("start".equalsIgnoreCase(action) || "stop".equalsIgnoreCase(action) || 
                "monitor".equalsIgnoreCase(action)) {
                allowed = true;
                reason = "Manager has permission for operational actions";
            } else {
                allowed = false;
                reason = "Manager lacks permission for administrative actions";
            }
        }
        // WORKER has limited permissions
        else if (farmer.getRole() == FarmerRole.WORKER) {
            if ("monitor".equalsIgnoreCase(action)) {
                allowed = true;
                reason = "Worker can monitor equipment";
            } else {
                allowed = false;
                reason = "Worker lacks permission for this action";
            }
        }
        
        return PermissionCheckResponse.builder()
                .farmerId(farmerId)
                .resourceId(resourceId)
                .action(action)
                .allowed(allowed)
                .reason(reason)
                .farmerRole(farmer.getRole().name())
                .build();
    }
    
    @Override
    public long getTotalFarmerCount() {
        return farmerRepository.getTotalFarmerCount();
    }
    
    @Override
    public long getFarmerCountByRole(FarmerRole role) {
        return farmerRepository.countByRole(role);
    }
}
