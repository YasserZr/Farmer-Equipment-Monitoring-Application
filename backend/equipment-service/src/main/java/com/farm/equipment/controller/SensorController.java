package com.farm.equipment.controller;

import com.farm.equipment.dto.request.CreateSensorRequest;
import com.farm.equipment.dto.request.UpdateSensorRequest;
import com.farm.equipment.dto.response.SensorDTO;
import com.farm.equipment.model.SensorType;
import com.farm.equipment.service.SensorService;
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
 * REST Controller for managing connected sensors.
 */
@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sensors", description = "Connected sensor management APIs")
public class SensorController {
    
    private final SensorService sensorService;
    
    @Operation(summary = "Create a new sensor", description = "Create a new connected sensor for a farm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sensor created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Valid @RequestBody CreateSensorRequest request) {
        
        log.info("POST /api/sensors - farmerId: {}, farmId: {}", farmerId, request.getFarmId());
        SensorDTO sensor = sensorService.createSensor(farmerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sensor);
    }
    
    @Operation(summary = "Get sensor by ID", description = "Retrieve a specific sensor by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensor found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sensor not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(
            @Parameter(description = "Sensor ID", required = true) @PathVariable UUID id) {
        
        log.info("GET /api/sensors/{}", id);
        SensorDTO sensor = sensorService.getSensorById(id);
        return ResponseEntity.ok(sensor);
    }
    
    @Operation(summary = "Get sensors by farm", description = "Retrieve all sensors for a specific farm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<Page<SensorDTO>> getSensorsByFarm(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/sensors/farm/{} - farmerId: {}", farmId, farmerId);
        Page<SensorDTO> sensors = sensorService.getSensorsByFarm(farmerId, farmId, pageable);
        return ResponseEntity.ok(sensors);
    }
    
    @Operation(summary = "Get sensors by type", description = "Retrieve sensors filtered by type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}/type/{type}")
    public ResponseEntity<Page<SensorDTO>> getSensorsByType(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @Parameter(description = "Sensor type", required = true) @PathVariable SensorType type,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/sensors/farm/{}/type/{} - farmerId: {}", farmId, type, farmerId);
        Page<SensorDTO> sensors = sensorService.getSensorsByType(farmerId, farmId, type, pageable);
        return ResponseEntity.ok(sensors);
    }
    
    @Operation(summary = "Get active sensors", description = "Retrieve all active sensors for a farm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}/active")
    public ResponseEntity<Page<SensorDTO>> getActiveSensors(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/sensors/farm/{}/active - farmerId: {}", farmId, farmerId);
        Page<SensorDTO> sensors = sensorService.getActiveSensors(farmerId, farmId, pageable);
        return ResponseEntity.ok(sensors);
    }
    
    @Operation(summary = "Get sensors with low battery", 
               description = "Retrieve sensors with battery level below 20%")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}/low-battery")
    public ResponseEntity<Page<SensorDTO>> getSensorsWithLowBattery(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/sensors/farm/{}/low-battery - farmerId: {}", farmId, farmerId);
        Page<SensorDTO> sensors = sensorService.getSensorsWithLowBattery(farmerId, farmId, pageable);
        return ResponseEntity.ok(sensors);
    }
    
    @Operation(summary = "Get offline sensors", 
               description = "Retrieve sensors that haven't communicated in the last 30 minutes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensors retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/farm/{farmId}/offline")
    public ResponseEntity<Page<SensorDTO>> getOfflineSensors(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Farm ID", required = true) @PathVariable UUID farmId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/sensors/farm/{}/offline - farmerId: {}", farmId, farmerId);
        Page<SensorDTO> sensors = sensorService.getOfflineSensors(farmerId, farmId, pageable);
        return ResponseEntity.ok(sensors);
    }
    
    @Operation(summary = "Update sensor", description = "Update an existing sensor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sensor updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sensor not found"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Sensor ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody UpdateSensorRequest request) {
        
        log.info("PUT /api/sensors/{} - farmerId: {}", id, farmerId);
        SensorDTO sensor = sensorService.updateSensor(farmerId, id, request);
        return ResponseEntity.ok(sensor);
    }
    
    @Operation(summary = "Update sensor battery", description = "Update the battery level of a sensor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Battery updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sensor not found"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @PatchMapping("/{id}/battery")
    public ResponseEntity<SensorDTO> updateBattery(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Sensor ID", required = true) @PathVariable UUID id,
            @Parameter(description = "Battery level (0-100)", required = true) @RequestParam Integer batteryLevel) {
        
        log.info("PATCH /api/sensors/{}/battery - farmerId: {}, level: {}%", id, farmerId, batteryLevel);
        SensorDTO sensor = sensorService.updateBattery(farmerId, id, batteryLevel);
        return ResponseEntity.ok(sensor);
    }
    
    @Operation(summary = "Update sensor communication", 
               description = "Update the last communication timestamp for a sensor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Communication timestamp updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SensorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sensor not found"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @PatchMapping("/{id}/communication")
    public ResponseEntity<SensorDTO> updateCommunication(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Sensor ID", required = true) @PathVariable UUID id) {
        
        log.info("PATCH /api/sensors/{}/communication - farmerId: {}", id, farmerId);
        SensorDTO sensor = sensorService.updateCommunication(farmerId, id);
        return ResponseEntity.ok(sensor);
    }
    
    @Operation(summary = "Delete sensor", description = "Delete a sensor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sensor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Sensor not found"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(
            @Parameter(description = "Farmer ID", required = true) @RequestHeader("X-Farmer-Id") UUID farmerId,
            @Parameter(description = "Sensor ID", required = true) @PathVariable UUID id) {
        
        log.info("DELETE /api/sensors/{} - farmerId: {}", id, farmerId);
        sensorService.deleteSensor(farmerId, id);
        return ResponseEntity.noContent().build();
    }
}
