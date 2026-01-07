package com.farmmonitoring.farmers.repository;

import com.farmmonitoring.farmers.entity.Farmer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Farmer Repository Integration Tests")
class FarmerRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private FarmerRepository farmerRepository;

    private Farmer farmer;

    @BeforeEach
    void setUp() {
        farmerRepository.deleteAll();

        farmer = new Farmer();
        farmer.setFirstName("John");
        farmer.setLastName("Doe");
        farmer.setEmail("john.doe@example.com");
        farmer.setPhone("+1234567890");
        farmer.setAddress("123 Farm Road");
        farmer.setCity("FarmVille");
        farmer.setCountry("USA");
        farmer.setActive(true);
    }

    @Test
    @DisplayName("Should save farmer successfully")
    void testSaveFarmer() {
        // When
        Farmer savedFarmer = farmerRepository.save(farmer);

        // Then
        assertThat(savedFarmer).isNotNull();
        assertThat(savedFarmer.getId()).isNotNull();
        assertThat(savedFarmer.getFirstName()).isEqualTo("John");
        assertThat(savedFarmer.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should find farmer by id")
    void testFindById() {
        // Given
        Farmer savedFarmer = farmerRepository.save(farmer);

        // When
        Optional<Farmer> foundFarmer = farmerRepository.findById(savedFarmer.getId());

        // Then
        assertThat(foundFarmer).isPresent();
        assertThat(foundFarmer.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should find all active farmers")
    void testFindByActive() {
        // Given
        farmerRepository.save(farmer);

        Farmer inactiveFarmer = new Farmer();
        inactiveFarmer.setFirstName("Jane");
        inactiveFarmer.setLastName("Smith");
        inactiveFarmer.setEmail("jane.smith@example.com");
        inactiveFarmer.setActive(false);
        farmerRepository.save(inactiveFarmer);

        // When
        List<Farmer> activeFarmers = farmerRepository.findByActive(true);

        // Then
        assertThat(activeFarmers).hasSize(1);
        assertThat(activeFarmers.get(0).getFirstName()).isEqualTo("John");
        assertThat(activeFarmers.get(0).isActive()).isTrue();
    }

    @Test
    @DisplayName("Should find farmer by email")
    void testFindByEmail() {
        // Given
        farmerRepository.save(farmer);

        // When
        Optional<Farmer> foundFarmer = farmerRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(foundFarmer).isPresent();
        assertThat(foundFarmer.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should search farmers by name")
    void testFindByNameContaining() {
        // Given
        farmerRepository.save(farmer);

        Farmer farmer2 = new Farmer();
        farmer2.setFirstName("Johnny");
        farmer2.setLastName("Appleseed");
        farmer2.setEmail("johnny@example.com");
        farmerRepository.save(farmer2);

        // When
        List<Farmer> foundFarmers = farmerRepository
                .findByFirstNameContainingOrLastNameContaining("John", "John");

        // Then
        assertThat(foundFarmers).hasSize(2);
    }

    @Test
    @DisplayName("Should update farmer successfully")
    void testUpdateFarmer() {
        // Given
        Farmer savedFarmer = farmerRepository.save(farmer);

        // When
        savedFarmer.setFirstName("Jane");
        savedFarmer.setEmail("jane.doe@example.com");
        Farmer updatedFarmer = farmerRepository.save(savedFarmer);

        // Then
        assertThat(updatedFarmer.getFirstName()).isEqualTo("Jane");
        assertThat(updatedFarmer.getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    @DisplayName("Should delete farmer successfully")
    void testDeleteFarmer() {
        // Given
        Farmer savedFarmer = farmerRepository.save(farmer);
        Long farmerId = savedFarmer.getId();

        // When
        farmerRepository.deleteById(farmerId);

        // Then
        Optional<Farmer> deletedFarmer = farmerRepository.findById(farmerId);
        assertThat(deletedFarmer).isEmpty();
    }

    @Test
    @DisplayName("Should count all farmers")
    void testCountFarmers() {
        // Given
        farmerRepository.save(farmer);

        Farmer farmer2 = new Farmer();
        farmer2.setFirstName("Jane");
        farmer2.setLastName("Smith");
        farmer2.setEmail("jane@example.com");
        farmerRepository.save(farmer2);

        // When
        long count = farmerRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }
}
