package com.farm.farmers.controller;

import com.farm.farmers.dto.request.CreateFarmerRequest;
import com.farm.farmers.dto.request.UpdateFarmerRequest;
import com.farm.farmers.dto.response.FarmerDTO;
import com.farm.farmers.dto.response.PermissionCheckResponse;
import com.farm.farmers.model.FarmerRole;
import com.farm.farmers.service.FarmerService;
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
 * REST controller for Farmer operations.
 * Provides endpoints for farmer management and permission checking.
 */
@RestController
@RequestMapping("/api/farmers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Farmers", description = "Farmer management operations")
public class FarmerController {
    
    private final FarmerService farmerService;
    
    @PostMapping
    @Operation(summary = "Create a new farmer", description = "Register a new farmer in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Farmer created successfully",
                    content = @Content(schema = @Schema(implementation = FarmerDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<FarmerDTO> createFarmer(
            @Valid @RequestBody CreateFarmerRequest request) {
        log.info("Received request to create farmer with email: {}", request.getEmail());
        FarmerDTO farmer = farmerService.createFarmer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(farmer);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get farmer by ID", description = "Retrieve a farmer by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farmer found",
                    content = @Content(schema = @Schema(implementation = FarmerDTO.class))),
        @ApiResponse(responseCode = "404", description = "Farmer not found")
    })
    public ResponseEntity<FarmerDTO> getFarmerById(
            @Parameter(description = "Farmer ID") @PathVariable UUID id) {
        log.debug("Received request to get farmer by ID: {}", id);
        FarmerDTO farmer = farmerService.getFarmerById(id);
        return ResponseEntity.ok(farmer);
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get farmer by email", description = "Retrieve a farmer by their email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farmer found",
                    content = @Content(schema = @Schema(implementation = FarmerDTO.class))),
        @ApiResponse(responseCode = "404", description = "Farmer not found")
    })
    public ResponseEntity<FarmerDTO> getFarmerByEmail(
            @Parameter(description = "Farmer email") @PathVariable String email) {
        log.debug("Received request to get farmer by email: {}", email);
        FarmerDTO farmer = farmerService.getFarmerByEmail(email);
        return ResponseEntity.ok(farmer);
    }
    
    @GetMapping
    @Operation(summary = "Get all farmers", description = "Retrieve all farmers with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farmers retrieved successfully")
    })
    public ResponseEntity<Page<FarmerDTO>> getAllFarmers(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("Received request to get all farmers");
        Page<FarmerDTO> farmers = farmerService.getAllFarmers(pageable);
        return ResponseEntity.ok(farmers);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search farmers by name", description = "Search farmers by name (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<Page<FarmerDTO>> searchFarmers(
            @Parameter(description = "Search term") @RequestParam String name,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("Received request to search farmers by name: {}", name);
        Page<FarmerDTO> farmers = farmerService.searchFarmersByName(name, pageable);
        return ResponseEntity.ok(farmers);
    }
    
    @GetMapping("/role/{role}")
    @Operation(summary = "Get farmers by role", description = "Retrieve all farmers with a specific role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farmers retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid role")
    })
    public ResponseEntity<Page<FarmerDTO>> getFarmersByRole(
            @Parameter(description = "Farmer role") @PathVariable FarmerRole role,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("Received request to get farmers by role: {}", role);
        Page<FarmerDTO> farmers = farmerService.getFarmersByRole(role, pageable);
        return ResponseEntity.ok(farmers);
    }
    
    @GetMapping("/admins")
    @Operation(summary = "Get farmers with admin privileges", 
               description = "Retrieve all farmers with admin privileges (OWNER or MANAGER)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin farmers retrieved successfully")
    })
    public ResponseEntity<Page<FarmerDTO>> getFarmersWithAdminPrivileges(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.debug("Received request to get farmers with admin privileges");
        Page<FarmerDTO> farmers = farmerService.getFarmersWithAdminPrivileges(pageable);
        return ResponseEntity.ok(farmers);
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Get recently registered farmers", 
               description = "Retrieve farmers registered within the specified number of days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent farmers retrieved successfully")
    })
    public ResponseEntity<Page<FarmerDTO>> getRecentlyRegisteredFarmers(
            @Parameter(description = "Number of days to look back") @RequestParam(defaultValue = "30") int days,
            @PageableDefault(size = 20, sort = "registrationDate") Pageable pageable) {
        log.debug("Received request to get farmers registered in last {} days", days);
        Page<FarmerDTO> farmers = farmerService.getRecentlyRegisteredFarmers(days, pageable);
        return ResponseEntity.ok(farmers);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update farmer", description = "Update farmer information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farmer updated successfully",
                    content = @Content(schema = @Schema(implementation = FarmerDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Farmer not found"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<FarmerDTO> updateFarmer(
            @Parameter(description = "Farmer ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateFarmerRequest request) {
        log.info("Received request to update farmer with ID: {}", id);
        FarmerDTO farmer = farmerService.updateFarmer(id, request);
        return ResponseEntity.ok(farmer);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete farmer", description = "Delete a farmer from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Farmer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Farmer not found")
    })
    public ResponseEntity<Void> deleteFarmer(
            @Parameter(description = "Farmer ID") @PathVariable UUID id) {
        log.info("Received request to delete farmer with ID: {}", id);
        farmerService.deleteFarmer(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if farmer exists", description = "Check if a farmer with the given ID exists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed")
    })
    public ResponseEntity<Boolean> farmerExists(
            @Parameter(description = "Farmer ID") @PathVariable UUID id) {
        log.debug("Received request to check if farmer exists: {}", id);
        boolean exists = farmerService.farmerExists(id);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/email/{email}/exists")
    @Operation(summary = "Check if email exists", description = "Check if a farmer with the given email exists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed")
    })
    public ResponseEntity<Boolean> emailExists(
            @Parameter(description = "Farmer email") @PathVariable String email) {
        log.debug("Received request to check if email exists: {}", email);
        boolean exists = farmerService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/{farmerId}/permissions")
    @Operation(summary = "Check farmer permissions", 
               description = "Verify if a farmer has permission to perform an action on a resource")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permission check completed",
                    content = @Content(schema = @Schema(implementation = PermissionCheckResponse.class))),
        @ApiResponse(responseCode = "404", description = "Farmer not found")
    })
    public ResponseEntity<PermissionCheckResponse> checkPermission(
            @Parameter(description = "Farmer ID") @PathVariable UUID farmerId,
            @Parameter(description = "Resource ID (e.g., pump ID)") @RequestParam UUID resourceId,
            @Parameter(description = "Action to perform") @RequestParam String action) {
        log.debug("Received permission check request for farmer: {}, resource: {}, action: {}", 
                  farmerId, resourceId, action);
        PermissionCheckResponse response = farmerService.checkPermission(farmerId, resourceId, action);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats/total")
    @Operation(summary = "Get total farmer count", description = "Retrieve the total number of farmers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> getTotalFarmerCount() {
        log.debug("Received request to get total farmer count");
        long count = farmerService.getTotalFarmerCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/role/{role}/count")
    @Operation(summary = "Get farmer count by role", description = "Retrieve the count of farmers by role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> getFarmerCountByRole(
            @Parameter(description = "Farmer role") @PathVariable FarmerRole role) {
        log.debug("Received request to get farmer count by role: {}", role);
        long count = farmerService.getFarmerCountByRole(role);
        return ResponseEntity.ok(count);
    }
}
