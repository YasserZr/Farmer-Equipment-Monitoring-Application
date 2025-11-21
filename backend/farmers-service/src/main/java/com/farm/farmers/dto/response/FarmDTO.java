package com.farm.farmers.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for farm response.
 * Contains all farm information for API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmDTO implements Serializable {
    
    private UUID id;
    private UUID farmerId;
    private String farmerName;
    private String name;
    private String location;
    private BigDecimal area;
    private String formattedArea;
    private boolean largeFarm;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
