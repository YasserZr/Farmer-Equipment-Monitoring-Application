package com.farm.equipment.controller;

import com.farm.equipment.dto.request.CreatePumpRequest;
import com.farm.equipment.dto.request.MaintenanceRequest;
import com.farm.equipment.dto.request.UpdatePumpRequest;
import com.farm.equipment.dto.response.PumpDTO;
import com.farm.equipment.model.EquipmentStatus;
import com.farm.equipment.service.PumpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for managing connected pumps.
 */
@RestController
@RequestMapping("/api/pumps")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pumps", description = "Connected pump management APIs")
public class PumpController {
    
    private final PumpService pumpService;
    
    @Operation(summary = "Create a new pump", description = "Create a new connected pump for a farm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pump created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PumpDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @PostMapping
    public ResponseEntity<PumpDTO> createPump(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Valid @RequestBody CreatePumpRequest request) {
        
        log.info("POST /api/pumps - farmerId: {}, farmId: {}", farmerId, request.getFarmId());
        PumpDTO pump = pumpService.createPump(farmerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pump);
    }
    
    @Operation(summary = "Get pump by ID", description = "Retrieve a specific pump by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pump found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PumpDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pump not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PumpDTO> getPumpById(
            @Parameter(description = "Pump ID", required = true) @PathVariable UUID id) {
        
        log.info("GET /api/pumps/{}", id);
        PumpDTO pump = pumpService.getPumpById(id);
        return ResponseEntity.ok(pump);
    }
    
    @Operation(summary = "Get pumps by farm", description = "Retrieve all pumps for a specific farm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pumps retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<Page<PumpDTO>> getPumpsByFarm(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/pumps/farm/{} - farmerId: {}", farmId, farmerId);
        Page<PumpDTO> pumps = pumpService.getPumpsByFarm(farmerId, farmId, pageable);
        return ResponseEntity.ok(pumps);
    }
    
    @Operation(summary = "Get pumps by status", description = "Retrieve pumps filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pumps retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}/status/{status}")
    public ResponseEntity<Page<PumpDTO>> getPumpsByStatus(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @Parameter(description = "Equipment status", required = true) @PathVariable EquipmentStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/pumps/farm/{}/status/{} - farmerId: {}", farmId, status, farmerId);
        Page<PumpDTO> pumps = pumpService.getPumpsByStatus(farmerId, farmId, status, pageable);
        return ResponseEntity.ok(pumps);
    }
    
    @Operation(summary = "Get operational pumps", description = "Retrieve all operational pumps for a farm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pumps retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}/operational")
    public ResponseEntity<Page<PumpDTO>> getOperationalPumps(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/pumps/farm/{}/operational - farmerId: {}", farmId, farmerId);
        Page<PumpDTO> pumps = pumpService.getOperationalPumps(farmerId, farmId, pageable);
        return ResponseEntity.ok(pumps);
    }
    
    @Operation(summary = "Get pumps with overdue maintenance", 
               description = "Retrieve pumps that have overdue maintenance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pumps retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}/maintenance-overdue")
    public ResponseEntity<Page<PumpDTO>> getPumpsWithOverdueMaintenance(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/pumps/farm/{}/maintenance-overdue - farmerId: {}", farmId, farmerId);
        Page<PumpDTO> pumps = pumpService.getPumpsWithOverdueMaintenance(farmerId, farmId, pageable);
        return ResponseEntity.ok(pumps);
    }
    
    @Operation(summary = "Update pump", description = "Update an existing pump")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pump updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PumpDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pump not found"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PumpDTO> updatePump(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Pump ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody UpdatePumpRequest request) {
        
        log.info("PUT /api/pumps/{} - farmerId: {}", id, farmerId);
        PumpDTO pump = pumpService.updatePump(farmerId, id, request);
        return ResponseEntity.ok(pump);
    }
    
    @Operation(summary = "Schedule maintenance", description = "Schedule maintenance for a pump")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance scheduled successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PumpDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pump not found"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @PostMapping("/{id}/maintenance/schedule")
    public ResponseEntity<PumpDTO> scheduleMaintenance(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Pump ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody MaintenanceRequest request) {
        
        log.info("POST /api/pumps/{}/maintenance/schedule - farmerId: {}", id, farmerId);
        PumpDTO pump = pumpService.scheduleMaintenance(farmerId, id, request);
        return ResponseEntity.ok(pump);
    }
    
    @Operation(summary = "Complete maintenance", description = "Mark maintenance as completed for a pump")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance completed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PumpDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pump not found"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @PostMapping("/{id}/maintenance/complete")
    public ResponseEntity<PumpDTO> completeMaintenance(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Pump ID", required = true) @PathVariable UUID id,
            @Parameter(description = "Maintenance notes") @RequestParam(required = false) String notes) {
        
        log.info("POST /api/pumps/{}/maintenance/complete - farmerId: {}", id, farmerId);
        PumpDTO pump = pumpService.completeMaintenance(farmerId, id, notes);
        return ResponseEntity.ok(pump);
    }
    
    @Operation(summary = "Delete pump", description = "Delete a pump")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pump deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Pump not found"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePump(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Pump ID", required = true) @PathVariable UUID id) {
        
        log.info("DELETE /api/pumps/{} - farmerId: {}", id, farmerId);
        pumpService.deletePump(farmerId, id);
        return ResponseEntity.noContent().build();
    }
}
