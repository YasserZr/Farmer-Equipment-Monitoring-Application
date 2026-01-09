package com.farm.equipment.controller;

import com.farm.equipment.dto.response.EquipmentStatisticsDTO;
import com.farm.equipment.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for equipment statistics.
 */
@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Equipment Statistics", description = "Equipment statistics and analytics APIs")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @Operation(summary = "Get equipment statistics", description = "Get overall equipment statistics including counts by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EquipmentStatisticsDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/statistics")
    public ResponseEntity<EquipmentStatisticsDTO> getStatistics() {
        log.info("Fetching equipment statistics");
        EquipmentStatisticsDTO statistics = statisticsService.getEquipmentStatistics();
        return ResponseEntity.ok(statistics);
    }
}
