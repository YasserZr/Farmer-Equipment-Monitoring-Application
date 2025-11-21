package com.farm.farmers.service;

import com.farm.farmers.dto.request.CreateFarmRequest;
import com.farm.farmers.dto.request.UpdateFarmRequest;
import com.farm.farmers.dto.response.FarmDTO;
import com.farm.farmers.exception.FarmNotFoundException;
import com.farm.farmers.exception.FarmerNotFoundException;
import com.farm.farmers.mapper.FarmMapper;
import com.farm.farmers.model.Farm;
import com.farm.farmers.repository.FarmRepository;
import com.farm.farmers.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementation of FarmService.
 * Provides business logic for farm management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FarmServiceImpl implements FarmService {
    
    private final FarmRepository farmRepository;
    private final FarmerRepository farmerRepository;
    private final FarmMapper farmMapper;
    
    @Override
    @Transactional
    public FarmDTO createFarm(CreateFarmRequest request) {
        log.info("Creating new farm for farmer ID: {}", request.getFarmerId());
        
        // Verify farmer exists
        if (!farmerRepository.existsById(request.getFarmerId())) {
            throw new FarmerNotFoundException(request.getFarmerId());
        }
        
        // Check for duplicate farm name for this farmer
        if (farmRepository.existsByFarmerIdAndName(request.getFarmerId(), request.getName())) {
            throw new IllegalArgumentException(
                String.format("Farm with name '%s' already exists for this farmer", request.getName())
            );
        }
        
        Farm farm = farmMapper.toEntity(request);
        farm.validateArea(); // Business rule validation
        
        Farm savedFarm = farmRepository.save(farm);
        
        log.info("Successfully created farm with ID: {}", savedFarm.getId());
        return farmMapper.toDTO(savedFarm);
    }
    
    @Override
    public FarmDTO getFarmById(UUID id) {
        log.debug("Fetching farm by ID: {}", id);
        Farm farm = farmRepository.findByIdWithFarmer(id)
                .orElseThrow(() -> new FarmNotFoundException(id));
        return farmMapper.toDTO(farm);
    }
    
    @Override
    public Page<FarmDTO> getAllFarms(Pageable pageable) {
        log.debug("Fetching all farms with pagination: {}", pageable);
        return farmRepository.findAll(pageable)
                .map(farmMapper::toDTO);
    }
    
    @Override
    public Page<FarmDTO> getFarmsByFarmerId(UUID farmerId, Pageable pageable) {
        log.debug("Fetching farms for farmer ID: {}", farmerId);
        
        // Verify farmer exists
        if (!farmerRepository.existsById(farmerId)) {
            throw new FarmerNotFoundException(farmerId);
        }
        
        return farmRepository.findByFarmerId(farmerId, pageable)
                .map(farmMapper::toDTO);
    }
    
    @Override
    public Page<FarmDTO> searchFarms(String searchTerm, Pageable pageable) {
        log.debug("Searching farms with term: {}", searchTerm);
        return farmRepository.searchFarms(searchTerm, pageable)
                .map(farmMapper::toDTO);
    }
    
    @Override
    public Page<FarmDTO> getFarmsByAreaRange(BigDecimal minArea, BigDecimal maxArea, Pageable pageable) {
        log.debug("Fetching farms with area between {} and {}", minArea, maxArea);
        return farmRepository.findByAreaBetween(minArea, maxArea, pageable)
                .map(farmMapper::toDTO);
    }
    
    @Override
    public Page<FarmDTO> getLargeFarms(Pageable pageable) {
        log.debug("Fetching large farms (area >= 100)");
        return farmRepository.findLargeFarms(pageable)
                .map(farmMapper::toDTO);
    }
    
    @Override
    @Transactional
    public FarmDTO updateFarm(UUID id, UpdateFarmRequest request) {
        log.info("Updating farm with ID: {}", id);
        
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new FarmNotFoundException(id));
        
        // Check for duplicate name if name is being updated
        if (request.getName() != null && !request.getName().equals(farm.getName())) {
            if (farmRepository.existsByFarmerIdAndName(farm.getFarmerId(), request.getName())) {
                throw new IllegalArgumentException(
                    String.format("Farm with name '%s' already exists for this farmer", request.getName())
                );
            }
        }
        
        farmMapper.updateEntityFromRequest(request, farm);
        
        // Validate area if it was updated
        if (request.getArea() != null) {
            farm.validateArea();
        }
        
        Farm updatedFarm = farmRepository.save(farm);
        
        log.info("Successfully updated farm with ID: {}", id);
        return farmMapper.toDTO(updatedFarm);
    }
    
    @Override
    @Transactional
    public void deleteFarm(UUID id) {
        log.info("Deleting farm with ID: {}", id);
        
        if (!farmRepository.existsById(id)) {
            throw new FarmNotFoundException(id);
        }
        
        farmRepository.deleteById(id);
        log.info("Successfully deleted farm with ID: {}", id);
    }
    
    @Override
    public boolean farmExists(UUID id) {
        return farmRepository.existsById(id);
    }
    
    @Override
    public BigDecimal getTotalAreaByFarmerId(UUID farmerId) {
        log.debug("Calculating total area for farmer ID: {}", farmerId);
        
        if (!farmerRepository.existsById(farmerId)) {
            throw new FarmerNotFoundException(farmerId);
        }
        
        BigDecimal totalArea = farmRepository.getTotalAreaByFarmerId(farmerId);
        return totalArea != null ? totalArea : BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal getAverageFarmArea() {
        log.debug("Calculating average farm area");
        BigDecimal avgArea = farmRepository.getAverageFarmArea();
        return avgArea != null ? avgArea : BigDecimal.ZERO;
    }
    
    @Override
    public long getTotalFarmCount() {
        return farmRepository.count();
    }
    
    @Override
    public long getFarmCountByFarmerId(UUID farmerId) {
        if (!farmerRepository.existsById(farmerId)) {
            throw new FarmerNotFoundException(farmerId);
        }
        return farmRepository.countByFarmerId(farmerId);
    }
}
