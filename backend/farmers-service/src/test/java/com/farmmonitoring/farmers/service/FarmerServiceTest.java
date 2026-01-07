package com.farmmonitoring.farmers.service;

import com.farmmonitoring.farmers.dto.FarmerDTO;
import com.farmmonitoring.farmers.entity.Farmer;
import com.farmmonitoring.farmers.exception.ResourceNotFoundException;
import com.farmmonitoring.farmers.mapper.FarmerMapper;
import com.farmmonitoring.farmers.repository.FarmerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Farmer Service Unit Tests")
class FarmerServiceTest {

    @Mock
    private FarmerRepository farmerRepository;

    @Mock
    private FarmerMapper farmerMapper;

    @InjectMocks
    private FarmerService farmerService;

    private Farmer farmer;
    private FarmerDTO farmerDTO;

    @BeforeEach
    void setUp() {
        farmer = new Farmer();
        farmer.setId(1L);
        farmer.setFirstName("John");
        farmer.setLastName("Doe");
        farmer.setEmail("john.doe@example.com");
        farmer.setPhone("+1234567890");
        farmer.setActive(true);

        farmerDTO = new FarmerDTO();
        farmerDTO.setId(1L);
        farmerDTO.setFirstName("John");
        farmerDTO.setLastName("Doe");
        farmerDTO.setEmail("john.doe@example.com");
        farmerDTO.setPhone("+1234567890");
        farmerDTO.setActive(true);
    }

    @Test
    @DisplayName("Should create farmer successfully")
    void testCreateFarmer_Success() {
        // Given
        when(farmerMapper.toEntity(any(FarmerDTO.class))).thenReturn(farmer);
        when(farmerRepository.save(any(Farmer.class))).thenReturn(farmer);
        when(farmerMapper.toDTO(any(Farmer.class))).thenReturn(farmerDTO);

        // When
        FarmerDTO result = farmerService.createFarmer(farmerDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        
        verify(farmerRepository, times(1)).save(any(Farmer.class));
        verify(farmerMapper, times(1)).toEntity(any(FarmerDTO.class));
        verify(farmerMapper, times(1)).toDTO(any(Farmer.class));
    }

    @Test
    @DisplayName("Should get farmer by id successfully")
    void testGetFarmerById_Success() {
        // Given
        when(farmerRepository.findById(1L)).thenReturn(Optional.of(farmer));
        when(farmerMapper.toDTO(any(Farmer.class))).thenReturn(farmerDTO);

        // When
        FarmerDTO result = farmerService.getFarmerById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
        
        verify(farmerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when farmer not found")
    void testGetFarmerById_NotFound() {
        // Given
        when(farmerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> farmerService.getFarmerById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Farmer not found with id: 999");
        
        verify(farmerRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all farmers with pagination")
    void testGetAllFarmers_WithPagination() {
        // Given
        List<Farmer> farmers = Arrays.asList(farmer, farmer);
        Page<Farmer> farmerPage = new PageImpl<>(farmers);
        Pageable pageable = PageRequest.of(0, 10);

        when(farmerRepository.findAll(pageable)).thenReturn(farmerPage);
        when(farmerMapper.toDTO(any(Farmer.class))).thenReturn(farmerDTO);

        // When
        Page<FarmerDTO> result = farmerService.getAllFarmers(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        
        verify(farmerRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should update farmer successfully")
    void testUpdateFarmer_Success() {
        // Given
        FarmerDTO updateDTO = new FarmerDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Smith");
        updateDTO.setEmail("jane.smith@example.com");

        Farmer updatedFarmer = new Farmer();
        updatedFarmer.setId(1L);
        updatedFarmer.setFirstName("Jane");
        updatedFarmer.setLastName("Smith");
        updatedFarmer.setEmail("jane.smith@example.com");

        when(farmerRepository.findById(1L)).thenReturn(Optional.of(farmer));
        when(farmerRepository.save(any(Farmer.class))).thenReturn(updatedFarmer);
        when(farmerMapper.toDTO(any(Farmer.class))).thenReturn(updateDTO);

        // When
        FarmerDTO result = farmerService.updateFarmer(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");
        
        verify(farmerRepository, times(1)).findById(1L);
        verify(farmerRepository, times(1)).save(any(Farmer.class));
    }

    @Test
    @DisplayName("Should delete farmer successfully")
    void testDeleteFarmer_Success() {
        // Given
        when(farmerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(farmerRepository).deleteById(1L);

        // When
        farmerService.deleteFarmer(1L);

        // Then
        verify(farmerRepository, times(1)).existsById(1L);
        verify(farmerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent farmer")
    void testDeleteFarmer_NotFound() {
        // Given
        when(farmerRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> farmerService.deleteFarmer(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Farmer not found with id: 999");
        
        verify(farmerRepository, times(1)).existsById(999L);
        verify(farmerRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should find farmers by active status")
    void testFindByActiveStatus() {
        // Given
        List<Farmer> activeFarmers = Arrays.asList(farmer);
        when(farmerRepository.findByActive(true)).thenReturn(activeFarmers);
        when(farmerMapper.toDTO(any(Farmer.class))).thenReturn(farmerDTO);

        // When
        List<FarmerDTO> result = farmerService.findByActiveStatus(true);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
        
        verify(farmerRepository, times(1)).findByActive(true);
    }

    @Test
    @DisplayName("Should search farmers by name")
    void testSearchFarmersByName() {
        // Given
        List<Farmer> foundFarmers = Arrays.asList(farmer);
        when(farmerRepository.findByFirstNameContainingOrLastNameContaining("John", "John"))
                .thenReturn(foundFarmers);
        when(farmerMapper.toDTO(any(Farmer.class))).thenReturn(farmerDTO);

        // When
        List<FarmerDTO> result = farmerService.searchByName("John");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        
        verify(farmerRepository, times(1))
                .findByFirstNameContainingOrLastNameContaining("John", "John");
    }
}
