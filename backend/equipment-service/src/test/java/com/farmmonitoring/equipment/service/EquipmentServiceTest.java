package com.farm.equipment.service;

import com.farm.equipment.dto.request.CreatePumpRequest;
import com.farm.equipment.dto.request.UpdatePumpRequest;
import com.farm.equipment.dto.response.PumpDTO;
import com.farm.equipment.model.ConnectedPump;
import com.farm.equipment.model.EquipmentStatus;
import com.farm.equipment.service.EquipmentEventPublisher;
import com.farm.equipment.exception.PumpNotFoundException;
import com.farm.equipment.mapper.PumpMapper;
import com.farm.equipment.repository.ConnectedPumpRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pump Service Unit Tests")
class PumpServiceTest {

    @Mock
    private ConnectedPumpRepository pumpRepository;

    @Mock
    private PumpMapper pumpMapper;

    @Mock
    private EquipmentEventPublisher eventPublisher;

    @InjectMocks
    private PumpService pumpService;

    private ConnectedPump pump;
    private PumpDTO pumpDTO;
    private CreatePumpRequest createRequest;
    private UUID farmerId;
    private UUID pumpId;

    @BeforeEach
    void setUp() {
        farmerId = UUID.randomUUID();
        pumpId = UUID.randomUUID();
        
        pump = ConnectedPump.builder()
                .id(pumpId)
                .farmId(UUID.randomUUID())
                .model("Model-X")
                .status(EquipmentStatus.ACTIVE)
                .maxFlow(new java.math.BigDecimal("100.0"))
                .location("Field A")
                .installationDate(LocalDateTime.now().minusMonths(6))
                .lastMaintenanceDate(LocalDateTime.now().minusDays(30))
                .build();

        pumpDTO = PumpDTO.builder()
                .id(pumpId)
                .farmId(pump.getFarmId())
                .model("Model-X")
                .status(EquipmentStatus.ACTIVE)
                .maxFlow(new java.math.BigDecimal("100.0"))
                .location("Field A")
                .build();
                
        createRequest = CreatePumpRequest.builder()
                .farmId(pump.getFarmId())
                .model("Model-X")
                .maxFlow(new java.math.BigDecimal("100.0"))
                .location("Field A")
                .installationDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create pump successfully")
    void testCreatePump_Success() {
        // Given
        when(pumpMapper.toEntity(any(CreatePumpRequest.class))).thenReturn(pump);
        when(pumpRepository.save(any(ConnectedPump.class))).thenReturn(pump);
        when(pumpMapper.toDTO(any(ConnectedPump.class))).thenReturn(pumpDTO);

        // When
        PumpDTO result = pumpService.createPump(farmerId, createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getModel()).isEqualTo("Model-X");
        assertThat(result.getStatus()).isEqualTo(EquipmentStatus.ACTIVE);

        verify(pumpRepository, times(1)).save(any(ConnectedPump.class));
    }

    @Test
    @DisplayName("Should update pump successfully")
    void testUpdatePump_Success() {
        // Given
        UpdatePumpRequest updateRequest = UpdatePumpRequest.builder()
                .status(EquipmentStatus.MAINTENANCE)
                .location("Field B")
                .build();

        when(pumpRepository.findById(pumpId)).thenReturn(Optional.of(pump));
        when(pumpRepository.save(any(ConnectedPump.class))).thenReturn(pump);
        when(pumpMapper.toDTO(any(ConnectedPump.class))).thenReturn(pumpDTO);

        // When
        PumpDTO result = pumpService.updatePump(farmerId, pumpId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(pumpRepository, times(1)).findById(pumpId);
        verify(pumpRepository, times(1)).save(any(ConnectedPump.class));
    }

    @Test
    @DisplayName("Should throw exception when pump not found")
    void testGetPumpById_NotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(pumpRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pumpService.getPumpById(nonExistentId))
                .isInstanceOf(PumpNotFoundException.class);

        verify(pumpRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should get pump by id successfully")
    void testGetPumpById_Success() {
        // Given
        when(pumpRepository.findById(pumpId)).thenReturn(Optional.of(pump));
        when(pumpMapper.toDTO(any(ConnectedPump.class))).thenReturn(pumpDTO);

        // When
        PumpDTO result = pumpService.getPumpById(pumpId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(pumpId);
        assertThat(result.getModel()).isEqualTo("Model-X");

        verify(pumpRepository, times(1)).findById(pumpId);
    }

    @Test
    @DisplayName("Should get pumps by farm id")
    void testGetPumpsByFarmId() {
        // Given
        UUID farmId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConnectedPump> pumpPage = new PageImpl<>(Arrays.asList(pump));
        
        when(pumpRepository.findByFarmId(farmId, pageable)).thenReturn(pumpPage);
        when(pumpMapper.toDTO(any(ConnectedPump.class))).thenReturn(pumpDTO);

        // When
        Page<PumpDTO> result = pumpService.getPumpsByFarm(farmerId, farmId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFarmId()).isEqualTo(pumpDTO.getFarmId());

        verify(pumpRepository, times(1)).findByFarmId(farmId, pageable);
    }

    @Test
    @DisplayName("Should delete pump successfully")
    void testDeletePump_Success() {
        // Given
        when(pumpRepository.findById(pumpId)).thenReturn(Optional.of(pump));
        doNothing().when(pumpRepository).delete(pump);

        // When
        pumpService.deletePump(farmerId, pumpId);

        // Then
        verify(pumpRepository, times(1)).findById(pumpId);
        verify(pumpRepository, times(1)).delete(pump);
    }
}
