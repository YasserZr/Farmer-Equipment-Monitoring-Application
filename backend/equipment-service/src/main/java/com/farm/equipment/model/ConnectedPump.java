package com.farm.equipment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a connected pump equipment.
 */
@Entity
@Table(name = "connected_pumps", indexes = {
    @Index(name = "idx_pump_farm_id", columnList = "farm_id"),
    @Index(name = "idx_pump_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"maintenanceHistory"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class ConnectedPump extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @NotNull(message = "Farm ID is required")
    @Column(name = "farm_id", nullable = false)
    private UUID farmId;
    
    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 100, message = "Model must be between 2 and 100 characters")
    @Column(name = "model", nullable = false, length = 100)
    private String model;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EquipmentStatus status;
    
    @NotNull(message = "Max flow is required")
    @DecimalMin(value = "0.01", message = "Max flow must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Max flow must be less than 100,000")
    @Digits(integer = 5, fraction = 2, message = "Max flow must have at most 5 digits before decimal and 2 after")
    @Column(name = "max_flow", nullable = false, precision = 7, scale = 2)
    private BigDecimal maxFlow;
    
    @Column(name = "location", length = 255)
    private String location;
    
    @Column(name = "installation_date")
    private LocalDateTime installationDate;
    
    @Column(name = "last_maintenance_date")
    private LocalDateTime lastMaintenanceDate;
    
    @Column(name = "next_maintenance_date")
    private LocalDateTime nextMaintenanceDate;
    
    @Column(name = "maintenance_notes", columnDefinition = "TEXT")
    private String maintenanceNotes;
    
    /**
     * Check if pump is operational
     * @return true if status is ACTIVE
     */
    public boolean isOperational() {
        return status != null && status.isOperational();
    }
    
    /**
     * Check if maintenance is overdue
     * @return true if next maintenance date is in the past
     */
    public boolean isMaintenanceOverdue() {
        return nextMaintenanceDate != null && nextMaintenanceDate.isBefore(LocalDateTime.now());
    }
    
    /**
     * Schedule next maintenance
     * @param daysFromNow days until next maintenance
     */
    public void scheduleNextMaintenance(int daysFromNow) {
        this.nextMaintenanceDate = LocalDateTime.now().plusDays(daysFromNow);
    }
    
    /**
     * Complete maintenance
     * @param notes maintenance notes
     */
    public void completeMaintenance(String notes) {
        this.lastMaintenanceDate = LocalDateTime.now();
        this.maintenanceNotes = notes;
        this.status = EquipmentStatus.ACTIVE;
    }
    
    /**
     * Set pump to maintenance mode
     */
    public void startMaintenance() {
        this.status = EquipmentStatus.MAINTENANCE;
    }
    
    /**
     * Get formatted max flow with unit
     * @return formatted string
     */
    public String getFormattedMaxFlow() {
        return maxFlow + " L/min";
    }
}
