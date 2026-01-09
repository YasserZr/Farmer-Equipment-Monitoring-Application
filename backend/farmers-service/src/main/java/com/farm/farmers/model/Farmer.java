package com.farm.farmers.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a Farmer in the system.
 * A farmer can own or manage one or more farms and has a specific role.
 */
@Entity
@Table(name = "farmers", indexes = {
    @Index(name = "idx_farmer_email", columnList = "email"),
    @Index(name = "idx_farmer_role", columnList = "role"),
    @Index(name = "idx_farmer_registration_date", columnList = "registration_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
@ToString(exclude = "farms")
@EqualsAndHashCode(callSuper = false, of = "id")
public class Farmer extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;
    
    @NotBlank(message = "Farmer name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$", 
             message = "Phone number format is invalid")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
    
    @NotNull(message = "Farmer role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private FarmerRole role;
    
    @NotNull(message = "Registration date is required")
    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;
    
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Farm> farms = new ArrayList<>();
    
    /**
     * Add a farm to this farmer's collection
     * @param farm the farm to add
     */
    public void addFarm(Farm farm) {
        farms.add(farm);
        farm.setFarmer(this);
    }
    
    /**
     * Remove a farm from this farmer's collection
     * @param farm the farm to remove
     */
    public void removeFarm(Farm farm) {
        farms.remove(farm);
        farm.setFarmer(null);
    }
    
    /**
     * Check if this farmer has administrative privileges
     * @return true if farmer has OWNER or MANAGER role
     */
    public boolean hasAdminPrivileges() {
        return role != null && role.hasAdminPrivileges();
    }
    
    /**
     * Check if this farmer can manage workers
     * @return true if farmer has OWNER or MANAGER role
     */
    public boolean canManageWorkers() {
        return role != null && role.canManageWorkers();
    }
    
    /**
     * Check if this farmer is an owner
     * @return true if farmer has OWNER role
     */
    public boolean isOwner() {
        return role != null && role.isOwner();
    }
    
    /**
     * Get the number of farms owned/managed by this farmer
     * @return count of farms
     */
    public int getFarmCount() {
        return farms != null ? farms.size() : 0;
    }
    
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }
}
