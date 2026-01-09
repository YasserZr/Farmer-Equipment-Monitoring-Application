package com.farm.equipment.controller;

import com.farm.equipment.dto.response.MaintenanceScheduleDTO;
import com.farm.equipment.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for maintenance operations.
 */
@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Maintenance", description = "Maintenance management endpoints")
public class MaintenanceController {
    
    private final MaintenanceService maintenanceService;
    
    /**
     * Get all maintenance schedules
     */
    @Operation(summary = "Get all maintenance schedules", description = "Retrieve all maintenance schedules with pagination")
    @GetMapping
    public ResponseEntity<List<MaintenanceScheduleDTO>> getAllMaintenanceSchedules(
            @PageableDefault(size = 100, sort = "scheduledDate") Pageable pageable) {
        log.info("GET /api/maintenance - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        List<MaintenanceScheduleDTO> schedules = maintenanceService.getAllMaintenanceSchedules(pageable);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get upcoming maintenance (due within next 30 days)
     */
    @Operation(summary = "Get upcoming maintenance", description = "Retrieve maintenance scheduled within the next 30 days")
    @GetMapping("/upcoming")
    public ResponseEntity<List<MaintenanceScheduleDTO>> getUpcomingMaintenance() {
        log.info("GET /api/maintenance/upcoming");
        
        List<MaintenanceScheduleDTO> schedules = maintenanceService.getUpcomingMaintenance();
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get overdue maintenance
     */
    @Operation(summary = "Get overdue maintenance", description = "Retrieve all overdue maintenance items")
    @GetMapping("/overdue")
    public ResponseEntity<List<MaintenanceScheduleDTO>> getOverdueMaintenance() {
        log.info("GET /api/maintenance/overdue");
        
        List<MaintenanceScheduleDTO> schedules = maintenanceService.getOverdueMaintenance();
        return ResponseEntity.ok(schedules);
    }
}
