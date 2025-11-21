package com.farm.farmers.repository;

import com.farm.farmers.model.Farmer;
import com.farm.farmers.model.FarmerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Farmer entity.
 * Provides CRUD operations and custom queries for farmer data access.
 */
@Repository
public interface FarmerRepository extends JpaRepository<Farmer, UUID> {
    
    /**
     * Find a farmer by email address
     * @param email the email to search for
     * @return Optional containing the farmer if found
     */
    Optional<Farmer> findByEmail(String email);
    
    /**
     * Check if a farmer exists with the given email
     * @param email the email to check
     * @return true if farmer exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all farmers with a specific role
     * @param role the farmer role
     * @return list of farmers with the specified role
     */
    List<Farmer> findByRole(FarmerRole role);
    
    /**
     * Find farmers by role and registered after a certain date
     * @param role the farmer role
     * @param date the registration date threshold
     * @return list of farmers matching criteria
     */
    List<Farmer> findByRoleAndRegistrationDateAfter(FarmerRole role, LocalDateTime date);
    
    /**
     * Find farmers whose names contain the search term (case-insensitive)
     * @param name the search term
     * @return list of matching farmers
     */
    List<Farmer> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find all farmers registered within a date range
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of farmers registered in the range
     */
    List<Farmer> findByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find farmers by phone number
     * @param phone the phone number
     * @return Optional containing the farmer if found
     */
    Optional<Farmer> findByPhone(String phone);
    
    /**
     * Count farmers by role
     * @param role the farmer role
     * @return count of farmers with the role
     */
    long countByRole(FarmerRole role);
    
    /**
     * Find farmers with admin privileges (OWNER or MANAGER)
     * @return list of farmers with admin roles
     */
    @Query("SELECT f FROM Farmer f WHERE f.role IN ('OWNER', 'MANAGER')")
    List<Farmer> findFarmersWithAdminPrivileges();
    
    /**
     * Find farmers who own/manage at least a certain number of farms
     * @param minFarmCount minimum number of farms
     * @return list of farmers with at least minFarmCount farms
     */
    @Query("SELECT f FROM Farmer f WHERE SIZE(f.farms) >= :minFarmCount")
    List<Farmer> findFarmersWithMinimumFarms(@Param("minFarmCount") int minFarmCount);
    
    /**
     * Find farmers registered in the last N days
     * @param days number of days to look back
     * @return list of recently registered farmers
     */
    @Query("SELECT f FROM Farmer f WHERE f.registrationDate >= :cutoffDate ORDER BY f.registrationDate DESC")
    List<Farmer> findRecentlyRegisteredFarmers(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Search farmers by multiple criteria
     * @param name partial name search
     * @param role farmer role
     * @param email email search
     * @return list of matching farmers
     */
    @Query("SELECT f FROM Farmer f WHERE " +
           "(:name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:role IS NULL OR f.role = :role) AND " +
           "(:email IS NULL OR LOWER(f.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<Farmer> searchFarmers(@Param("name") String name, 
                               @Param("role") FarmerRole role, 
                               @Param("email") String email);
    
    /**
     * Find farmers with their farms eagerly loaded
     * @param farmerId the farmer ID
     * @return Optional containing the farmer with farms
     */
    @Query("SELECT f FROM Farmer f LEFT JOIN FETCH f.farms WHERE f.id = :farmerId")
    Optional<Farmer> findByIdWithFarms(@Param("farmerId") UUID farmerId);
    
    /**
     * Get total count of all farmers
     * @return total farmer count
     */
    @Query("SELECT COUNT(f) FROM Farmer f")
    long getTotalFarmerCount();
}
