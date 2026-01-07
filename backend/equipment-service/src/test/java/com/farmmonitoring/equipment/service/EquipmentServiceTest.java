package com.farmmonitoring.equipment.service;

import com.farmmonitoring.equipment.dto.PumpDTO;
import com.farmmonitoring.equipment.dto.PumpStatusUpdateDTO;
import com.farmmonitoring.equipment.entity.Pump;
import com.farmmonitoring.equipment.enums.EquipmentStatus;
import com.farmmonitoring.equipment.event.EquipmentEventPublisher;
import com.farmmonitoring.equipment.exception.ResourceNotFoundException;
import com.farmmonitoring.equipment.mapper.PumpMapper;
import com.farmmonitoring.equipment.repository.PumpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Equipment Service Unit Tests")
class EquipmentServiceTest {

    @Mock
    private PumpRepository pumpRepository;

    @Mock
    private PumpMapper pumpMapper;

    @Mock
    private EquipmentEventPublisher eventPublisher;

    @InjectMocks
    private EquipmentService equipmentService;

    private Pump pump;
    private PumpDTO pumpDTO;

    @BeforeEach
    void setUp() {
        pump = new Pump();
        pump.setId(1L);
        pump.setSerialNumber("PUMP-001");
        pump.setModel("Model-X");
        pump.setStatus(EquipmentStatus.ACTIVE);
        pump.setFarmerId(1L);
        pump.setLatitude(40.7128);
        pump.setLongitude(-74.0060);
        pump.setLastMaintenanceDate(LocalDateTime.now().minusDays(30));

        pumpDTO = new PumpDTO();
        pumpDTO.setId(1L);
        pumpDTO.setSerialNumber("PUMP-001");
        pumpDTO.setModel("Model-X");
        pumpDTO.setStatus("ACTIVE");
        pumpDTO.setFarmerId(1L);
    }

    @Test
    @DisplayName("Should create pump successfully")
    void testCreatePump_Success() {
        // Given
        when(pumpMapper.toEntity(any(PumpDTO.class))).thenReturn(pump);
        when(pumpRepository.save(any(Pump.class))).thenReturn(pump);
        when(pumpMapper.toDTO(any(Pump.class))).thenReturn(pumpDTO);
        doNothing().when(eventPublisher).publishEquipmentCreated(any(Pump.class));

        // When
        PumpDTO result = equipmentService.createPump(pumpDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSerialNumber()).isEqualTo("PUMP-001");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");

        verify(pumpRepository, times(1)).save(any(Pump.class));
        verify(eventPublisher, times(1)).publishEquipmentCreated(any(Pump.class));
    }

    @Test
    @DisplayName("Should update pump status successfully")
    void testUpdatePumpStatus_Success() {
        // Given
        PumpStatusUpdateDTO statusUpdate = new PumpStatusUpdateDTO();
        statusUpdate.setStatus("MAINTENANCE");
        statusUpdate.setReason("Scheduled maintenance");

        when(pumpRepository.findById(1L)).thenReturn(Optional.of(pump));
        when(pumpRepository.save(any(Pump.class))).thenReturn(pump);
        doNothing().when(eventPublisher).publishStatusChanged(any(Pump.class), anyString());

        // When
        equipmentService.updatePumpStatus(1L, statusUpdate);

        // Then
        verify(pumpRepository, times(1)).findById(1L);
        verify(pumpRepository, times(1)).save(any(Pump.class));
        verify(eventPublisher, times(1)).publishStatusChanged(any(Pump.class), eq("ACTIVE"));
    }

    @Test
    @DisplayName("Should throw exception when pump not found for status update")
    void testUpdatePumpStatus_NotFound() {
        // Given
        PumpStatusUpdateDTO statusUpdate = new PumpStatusUpdateDTO();
        statusUpdate.setStatus("MAINTENANCE");

        when(pumpRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> equipmentService.updatePumpStatus(999L, statusUpdate))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pump not found with id: 999");

        verify(pumpRepository, times(1)).findById(999L);
        verify(eventPublisher, never()).publishStatusChanged(any(), anyString());
    }

    @Test
    @DisplayName("Should get pump by id successfully")
    void testGetPumpById_Success() {
        // Given
        when(pumpRepository.findById(1L)).thenReturn(Optional.of(pump));
        when(pumpMapper.toDTO(any(Pump.class))).thenReturn(pumpDTO);

        // When
        PumpDTO result = equipmentService.getPumpById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSerialNumber()).isEqualTo("PUMP-001");

        verify(pumpRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should get pumps by farmer id")
    void testGetPumpsByFarmerId() {
        // Given
        List<Pump> pumps = Arrays.asList(pump);
        when(pumpRepository.findByFarmerId(1L)).thenReturn(pumps);
        when(pumpMapper.toDTO(any(Pump.class))).thenReturn(pumpDTO);

        // When
        List<PumpDTO> result = equipmentService.getPumpsByFarmerId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFarmerId()).isEqualTo(1L);

        verify(pumpRepository, times(1)).findByFarmerId(1L);
    }

    @Test
    @DisplayName("Should get pumps by status")
    void testGetPumpsByStatus() {
        // Given
        List<Pump> activePumps = Arrays.asList(pump);
        when(pumpRepository.findByStatus(EquipmentStatus.ACTIVE)).thenReturn(activePumps);
        when(pumpMapper.toDTO(any(Pump.class))).thenReturn(pumpDTO);

        // When
        List<PumpDTO> result = equipmentService.getPumpsByStatus(EquipmentStatus.ACTIVE);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");

        verify(pumpRepository, times(1)).findByStatus(EquipmentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should delete pump successfully")
    void testDeletePump_Success() {
        // Given
        when(pumpRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pumpRepository).deleteById(1L);
        doNothing().when(eventPublisher).publishEquipmentDeleted(1L);

        // When
        equipmentService.deletePump(1L);

        // Then
        verify(pumpRepository, times(1)).existsById(1L);
        verify(pumpRepository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishEquipmentDeleted(1L);
    }

    @Test
    @DisplayName("Should find pumps needing maintenance")
    void testFindPumpsNeedingMaintenance() {
        // Given
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Pump> pumpsNeedingMaintenance = Arrays.asList(pump);
        
        when(pumpRepository.findByLastMaintenanceDateBefore(any(LocalDateTime.class)))
                .thenReturn(pumpsNeedingMaintenance);
        when(pumpMapper.toDTO(any(Pump.class))).thenReturn(pumpDTO);

        // When
        List<PumpDTO> result = equipmentService.findPumpsNeedingMaintenance(30);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(pumpRepository, times(1)).findByLastMaintenanceDateBefore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should count pumps by status")
    void testCountPumpsByStatus() {
        // Given
        when(pumpRepository.countByStatus(EquipmentStatus.ACTIVE)).thenReturn(5L);

        // When
        Long count = equipmentService.countPumpsByStatus(EquipmentStatus.ACTIVE);

        // Then
        assertThat(count).isEqualTo(5L);

        verify(pumpRepository, times(1)).countByStatus(EquipmentStatus.ACTIVE);
    }
}
