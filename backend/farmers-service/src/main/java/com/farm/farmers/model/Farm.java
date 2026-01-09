package com.farm.farmers.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing a Farm in the system.
 * A farm belongs to a farmer and has specific location and area details.
 */
@Entity
@Table(name = "farms", indexes = {
    @Index(name = "idx_farm_farmer_id", columnList = "farmer_id"),
    @Index(name = "idx_farm_name", columnList = "name"),
    @Index(name = "idx_farm_location", columnList = "location")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
@ToString(exclude = "farmer")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Farm extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;
    
    @NotNull(message = "Farmer ID is required")
    @Column(name = "farmer_id", nullable = false, columnDefinition = "uuid")
    private UUID farmerId;
    
    @NotBlank(message = "Farm name is required")
    @Size(min = 2, max = 150, message = "Farm name must be between 2 and 150 characters")
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    
    @NotBlank(message = "Location is required")
    @Size(min = 3, max = 255, message = "Location must be between 3 and 255 characters")
    @Column(name = "location", nullable = false, length = 255)
    private String location;
    
    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.01", message = "Area must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Area must be less than 1,000,000")
    @Digits(integer = 6, fraction = 2, message = "Area must have at most 6 digits before decimal and 2 after")
    @Column(name = "area", nullable = false, precision = 8, scale = 2)
    private BigDecimal area;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmer_id", nullable = false, insertable = false, updatable = false, 
                foreignKey = @ForeignKey(name = "fk_farm_farmer"))
    private Farmer farmer;
    
    /**
     * Validate that the area is positive
     * @throws IllegalArgumentException if area is not positive
     */
    public void validateArea() {
        if (area == null || area.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Farm area must be greater than zero");
        }
    }
    
    /**
     * Check if this farm is considered large (area > 100 hectares)
     * @return true if farm area exceeds 100
     */
    public boolean isLargeFarm() {
        return area != null && area.compareTo(new BigDecimal("100.00")) > 0;
    }
    
    /**
     * Get a formatted string representation of the farm area
     * @return formatted area string with unit
     */
    public String getFormattedArea() {
        return area != null ? String.format("%.2f hectares", area) : "N/A";
    }
    
    /**
     * Validate farm data before persistence
     */
    @PrePersist
    @PreUpdate
    protected void validate() {
        super.onCreate();
        validateArea();
        if (name != null) {
            name = name.trim();
        }
        if (location != null) {
            location = location.trim();
        }
    }
}
