package com.farm.supervision.controller;

import com.farm.supervision.dto.DashboardStatisticsDTO;
import com.farm.supervision.service.StatisticsService;
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
 * REST Controller for event statistics and dashboard.
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "Event statistics and dashboard APIs")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @Operation(summary = "Get dashboard statistics", 
               description = "Retrieve comprehensive dashboard statistics including event counts, recent events, and trends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = DashboardStatisticsDTO.class)))
    })
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatisticsDTO> getDashboardStatistics() {
        log.info("GET /api/statistics/dashboard");
        DashboardStatisticsDTO statistics = statisticsService.getDashboardStatistics();
        return ResponseEntity.ok(statistics);
    }
}
