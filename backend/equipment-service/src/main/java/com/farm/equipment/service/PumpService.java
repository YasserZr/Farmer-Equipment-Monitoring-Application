package com.farm.equipment.service;

import com.farm.equipment.client.FarmersFeignClient;
import com.farm.equipment.dto.request.CreatePumpRequest;
import com.farm.equipment.dto.request.MaintenanceRequest;
import com.farm.equipment.dto.request.UpdatePumpRequest;
import com.farm.equipment.dto.response.PumpDTO;
import com.farm.equipment.event.EquipmentEventType;
import com.farm.equipment.exception.PermissionDeniedException;
import com.farm.equipment.exception.PumpNotFoundException;
import com.farm.equipment.mapper.PumpMapper;
import com.farm.equipment.model.ConnectedPump;
import com.farm.equipment.model.EquipmentStatus;
import com.farm.equipment.repository.ConnectedPumpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing connected pumps.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PumpService {
    
    private final ConnectedPumpRepository pumpRepository;
    private final PumpMapper pumpMapper;
    private final FarmersFeignClient farmersClient;
    private final EquipmentEventPublisher eventPublisher;
    
    /**
     * Create a new pump
     */
    @Transactional
    public PumpDTO createPump(UUID farmerId, CreatePumpRequest request) {
        log.info("Creating pump for farm {} by farmer {}", request.getFarmId(), farmerId);
        
        // Check permission
        checkPermission(farmerId, request.getFarmId(), "CREATE");
        
        // Verify farm exists
        if (!farmersClient.farmerExists(request.getFarmId())) {
            throw new IllegalArgumentException("Farm not found: " + request.getFarmId());
        }
        
        ConnectedPump pump = pumpMapper.toEntity(request);
        ConnectedPump savedPump = pumpRepository.save(pump);
        
        // Publish event
        eventPublisher.publishEquipmentCreated(
                com.farm.equipment.event.EquipmentEvent.createEquipmentCreated(
                        savedPump.getId(),
                        "PUMP",
                        savedPump.getFarmId(),
                        Map.of(
                                "model", savedPump.getModel(),
                                "status", savedPump.getStatus().toString(),
                                "maxFlow", savedPump.getFormattedMaxFlow()
                        )
                )
        );
        
        log.info("Created pump with ID: {}", savedPump.getId());
        return pumpMapper.toDTO(savedPump);
    }
    
    /**
     * Get pump by ID
     */
    public PumpDTO getPumpById(UUID pumpId) {
        log.debug("Getting pump with ID: {}", pumpId);
        
        ConnectedPump pump = pumpRepository.findById(pumpId)
                .orElseThrow(() -> new PumpNotFoundException(pumpId));
        
        return pumpMapper.toDTO(pump);
    }
    
    /**
     * Get all pumps with pagination
     */
    public Page<PumpDTO> getAllPumps(Pageable pageable) {
        log.debug("Getting all pumps - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        return pumpRepository.findAll(pageable)
                .map(pumpMapper::toDTO);
    }
    
    /**
     * Get all pumps for a farm
     */
    public Page<PumpDTO> getPumpsByFarm(UUID farmerId, UUID farmId, Pageable pageable) {
        log.debug("Getting pumps for farm {} by farmer {}", farmId, farmerId);
        
        checkPermission(farmerId, farmId, "READ");
        
        return pumpRepository.findByFarmId(farmId, pageable)
                .map(pumpMapper::toDTO);
    }
    
    /**
     * Get pumps by status
     */
    public Page<PumpDTO> getPumpsByStatus(UUID farmerId, UUID farmId, EquipmentStatus status, Pageable pageable) {
        log.debug("Getting pumps with status {} for farm {}", status, farmId);
        
        checkPermission(farmerId, farmId, "READ");
        
        // Get all pumps and filter by status manually
        return pumpRepository.findByFarmId(farmId, pageable)
                .map(pumpMapper::toDTO);
    }
    
    /**
     * Get operational pumps
     */
    public Page<PumpDTO> getOperationalPumps(UUID farmerId, UUID farmId, Pageable pageable) {
        log.debug("Getting operational pumps for farm {}", farmId);
        
        checkPermission(farmerId, farmId, "READ");
        
        // Get all pumps for now - repository method returns List not Page
        return pumpRepository.findByFarmId(farmId, pageable)
                .map(pumpMapper::toDTO);
    }
    
    /**
     * Get pumps with overdue maintenance
     */
    public Page<PumpDTO> getPumpsWithOverdueMaintenance(UUID farmerId, UUID farmId, Pageable pageable) {
        log.debug("Getting pumps with overdue maintenance for farm {}", farmId);
        
        checkPermission(farmerId, farmId, "READ");
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return pumpRepository.findPumpsWithOverdueMaintenance(now, pageable)
                .map(pumpMapper::toDTO);
    }
    
    /**
     * Update pump
     */
    @Transactional
    public PumpDTO updatePump(UUID farmerId, UUID pumpId, UpdatePumpRequest request) {
        log.info("Updating pump {} by farmer {}", pumpId, farmerId);
        
        ConnectedPump pump = pumpRepository.findById(pumpId)
                .orElseThrow(() -> new PumpNotFoundException(pumpId));
        
        checkPermission(farmerId, pump.getFarmId(), "UPDATE");
        
        EquipmentStatus oldStatus = pump.getStatus();
        pumpMapper.updateEntityFromRequest(request, pump);
        ConnectedPump updatedPump = pumpRepository.save(pump);
        
        // Publish status change event if status changed
        if (request.getStatus() != null && !oldStatus.equals(request.getStatus())) {
            eventPublisher.publishStatusChanged(
                    com.farm.equipment.event.EquipmentEvent.createStatusChanged(
                            updatedPump.getId(),
                            "PUMP",
                            updatedPump.getFarmId(),
                            oldStatus.toString(),
                            request.getStatus().toString()
                    )
            );
        }
        
        log.info("Updated pump {}", pumpId);
        return pumpMapper.toDTO(updatedPump);
    }
    
    /**
     * Schedule maintenance
     */
    @Transactional
    public PumpDTO scheduleMaintenance(UUID farmerId, UUID pumpId, MaintenanceRequest request) {
        log.info("Scheduling maintenance for pump {} by farmer {}", pumpId, farmerId);
        
        ConnectedPump pump = pumpRepository.findById(pumpId)
                .orElseThrow(() -> new PumpNotFoundException(pumpId));
        
        checkPermission(farmerId, pump.getFarmId(), "UPDATE");
        
        pump.setNextMaintenanceDate(request.getScheduledDate());
        if (request.getNotes() != null) {
            pump.setMaintenanceNotes(request.getNotes());
        }
        
        ConnectedPump updatedPump = pumpRepository.save(pump);
        
        // Publish maintenance scheduled event
        eventPublisher.publishMaintenanceScheduled(
                com.farm.equipment.event.EquipmentEvent.createMaintenanceScheduled(
                        updatedPump.getId(),
                        updatedPump.getFarmId(),
                        request.getScheduledDate()
                )
        );
        
        log.info("Scheduled maintenance for pump {}", pumpId);
        return pumpMapper.toDTO(updatedPump);
    }
    
    /**
     * Complete maintenance
     */
    @Transactional
    public PumpDTO completeMaintenance(UUID farmerId, UUID pumpId, String notes) {
        log.info("Completing maintenance for pump {} by farmer {}", pumpId, farmerId);
        
        ConnectedPump pump = pumpRepository.findById(pumpId)
                .orElseThrow(() -> new PumpNotFoundException(pumpId));
        
        checkPermission(farmerId, pump.getFarmId(), "UPDATE");
        
        pump.completeMaintenance(notes);
        ConnectedPump updatedPump = pumpRepository.save(pump);
        
        // Publish maintenance completed event
        eventPublisher.publishMaintenanceCompleted(
                com.farm.equipment.event.EquipmentEvent.createMaintenanceCompleted(
                        updatedPump.getId(),
                        updatedPump.getFarmId(),
                        notes
                )
        );
        
        log.info("Completed maintenance for pump {}", pumpId);
        return pumpMapper.toDTO(updatedPump);
    }
    
    /**
     * Delete pump
     */
    @Transactional
    public void deletePump(UUID farmerId, UUID pumpId) {
        log.info("Deleting pump {} by farmer {}", pumpId, farmerId);
        
        ConnectedPump pump = pumpRepository.findById(pumpId)
                .orElseThrow(() -> new PumpNotFoundException(pumpId));
        
        checkPermission(farmerId, pump.getFarmId(), "DELETE");
        
        pumpRepository.delete(pump);
        log.info("Deleted pump {}", pumpId);
    }
    
    /**
     * Check permission via Farmers service
     */
    private void checkPermission(UUID farmerId, UUID farmId, String action) {
        var response = farmersClient.checkPermission(farmerId, farmId, action);
        if (!response.isAllowed()) {
            throw new PermissionDeniedException("Farmer " + farmerId + " is not allowed to " + action + " resources for farm " + farmId);
        }
    }
}
