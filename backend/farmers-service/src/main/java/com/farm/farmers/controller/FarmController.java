package com.farm.farmers.controller;

import com.farm.farmers.dto.request.CreateFarmRequest;
import com.farm.farmers.dto.request.UpdateFarmRequest;
import com.farm.farmers.dto.response.FarmDTO;
import com.farm.farmers.service.FarmService;
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

import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST controller for Farm operations.
 * Provides endpoints for farm management.
 */
@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Farms", description = "Farm management operations")
public class FarmController {
    
    private final FarmService farmService;
    
    @PostMapping
    @Operation(summary = "Create a new farm", description = "Register a new farm for a farmer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Farm created successfully",
                    content = @Content(schema = @Schema(implementation = FarmDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Farmer not found"),
        @ApiResponse(responseCode = "409", description = "Farm name already exists for this farmer")
    })
    public ResponseEntity<FarmDTO> createFarm(
            @Valid @RequestBody CreateFarmRequest request) {
        log.info("Received request to create farm for farmer ID: {}", request.getFarmerId());
        FarmDTO farm = farmService.createFarm(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(farm);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get farm by ID", description = "Retrieve a farm by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farm found",
                    content = @Content(schema = @Schema(implementation = FarmDTO.class))),
        @ApiResponse(responseCode = "404", description = "Farm not found")
    })
    public ResponseEntity<FarmDTO> getFarmById(
            @Parameter(description = "Farm ID") @PathVariable UUID id) {
        log.debug("Received request to get farm by ID: {}", id);
        FarmDTO farm = farmService.getFarmById(id);
        return ResponseEntity.ok(farm);
    }
    
    @GetMapping
    @Operation(summary = "Get all farms", description = "Retrieve all farms with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farms retrieved successfully")
    })
    public ResponseEntity<Page<FarmDTO>> getAllFarms(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("Received request to get all farms");
        Page<FarmDTO> farms = farmService.getAllFarms(pageable);
        return ResponseEntity.ok(farms);
    }
    
    @GetMapping("/farmer/{farmerId}")
    @Operation(summary = "Get farms by farmer ID", description = "Retrieve all farms belonging to a specific farmer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farms retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Farmer not found")
    })
    public ResponseEntity<Page<FarmDTO>> getFarmsByFarmerId(
            @Parameter(description = "Farmer ID") @PathVariable UUID farmerId,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("Received request to get farms for farmer ID: {}", farmerId);
        Page<FarmDTO> farms = farmService.getFarmsByFarmerId(farmerId, pageable);
        return ResponseEntity.ok(farms);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search farms", description = "Search farms by name or location (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<Page<FarmDTO>> searchFarms(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("Received request to search farms with term: {}", searchTerm);
        Page<FarmDTO> farms = farmService.searchFarms(searchTerm, pageable);
        return ResponseEntity.ok(farms);
    }
    
    @GetMapping("/area-range")
    @Operation(summary = "Get farms by area range", 
               description = "Retrieve farms within a specified area range (in hectares)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farms retrieved successfully")
    })
    public ResponseEntity<Page<FarmDTO>> getFarmsByAreaRange(
            @Parameter(description = "Minimum area") @RequestParam BigDecimal minArea,
            @Parameter(description = "Maximum area") @RequestParam BigDecimal maxArea,
            @PageableDefault(size = 20, sort = "area") Pageable pageable) {
        log.debug("Received request to get farms with area between {} and {}", minArea, maxArea);
        Page<FarmDTO> farms = farmService.getFarmsByAreaRange(minArea, maxArea, pageable);
        return ResponseEntity.ok(farms);
    }
    
    @GetMapping("/large")
    @Operation(summary = "Get large farms", description = "Retrieve all large farms (area >= 100 hectares)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Large farms retrieved successfully")
    })
    public ResponseEntity<Page<FarmDTO>> getLargeFarms(
            @PageableDefault(size = 20, sort = "area") Pageable pageable) {
        log.debug("Received request to get large farms");
        Page<FarmDTO> farms = farmService.getLargeFarms(pageable);
        return ResponseEntity.ok(farms);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update farm", description = "Update farm information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farm updated successfully",
                    content = @Content(schema = @Schema(implementation = FarmDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Farm not found"),
        @ApiResponse(responseCode = "409", description = "Farm name already exists for this farmer")
    })
    public ResponseEntity<FarmDTO> updateFarm(
            @Parameter(description = "Farm ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateFarmRequest request) {
        log.info("Received request to update farm with ID: {}", id);
        FarmDTO farm = farmService.updateFarm(id, request);
        return ResponseEntity.ok(farm);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete farm", description = "Delete a farm from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Farm deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Farm not found")
    })
    public ResponseEntity<Void> deleteFarm(
            @Parameter(description = "Farm ID") @PathVariable UUID id) {
        log.info("Received request to delete farm with ID: {}", id);
        farmService.deleteFarm(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if farm exists", description = "Check if a farm with the given ID exists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed")
    })
    public ResponseEntity<Boolean> farmExists(
            @Parameter(description = "Farm ID") @PathVariable UUID id) {
        log.debug("Received request to check if farm exists: {}", id);
        boolean exists = farmService.farmExists(id);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/farmer/{farmerId}/total-area")
    @Operation(summary = "Get total area by farmer", 
               description = "Calculate the total area of all farms for a specific farmer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total area calculated successfully"),
        @ApiResponse(responseCode = "404", description = "Farmer not found")
    })
    public ResponseEntity<BigDecimal> getTotalAreaByFarmerId(
            @Parameter(description = "Farmer ID") @PathVariable UUID farmerId) {
        log.debug("Received request to get total area for farmer ID: {}", farmerId);
        BigDecimal totalArea = farmService.getTotalAreaByFarmerId(farmerId);
        return ResponseEntity.ok(totalArea);
    }
    
    @GetMapping("/stats/average-area")
    @Operation(summary = "Get average farm area", description = "Calculate the average area of all farms")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Average area calculated successfully")
    })
    public ResponseEntity<BigDecimal> getAverageFarmArea() {
        log.debug("Received request to get average farm area");
        BigDecimal avgArea = farmService.getAverageFarmArea();
        return ResponseEntity.ok(avgArea);
    }
    
    @GetMapping("/stats/total")
    @Operation(summary = "Get total farm count", description = "Retrieve the total number of farms")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> getTotalFarmCount() {
        log.debug("Received request to get total farm count");
        long count = farmService.getTotalFarmCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/farmer/{farmerId}/count")
    @Operation(summary = "Get farm count by farmer", 
               description = "Retrieve the number of farms for a specific farmer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Farmer not found")
    })
    public ResponseEntity<Long> getFarmCountByFarmerId(
            @Parameter(description = "Farmer ID") @PathVariable UUID farmerId) {
        log.debug("Received request to get farm count for farmer ID: {}", farmerId);
        long count = farmService.getFarmCountByFarmerId(farmerId);
        return ResponseEntity.ok(count);
    }
}
