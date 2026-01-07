package com.farmmonitoring.farmers.controller;

import com.farmmonitoring.farmers.dto.FarmerDTO;
import com.farmmonitoring.farmers.service.FarmerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FarmerController.class)
@DisplayName("Farmer Controller REST API Tests")
class FarmerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FarmerService farmerService;

    private FarmerDTO farmerDTO;

    @BeforeEach
    void setUp() {
        farmerDTO = new FarmerDTO();
        farmerDTO.setId(1L);
        farmerDTO.setFirstName("John");
        farmerDTO.setLastName("Doe");
        farmerDTO.setEmail("john.doe@example.com");
        farmerDTO.setPhone("+1234567890");
        farmerDTO.setAddress("123 Farm Road");
        farmerDTO.setCity("FarmVille");
        farmerDTO.setCountry("USA");
        farmerDTO.setActive(true);
    }

    @Test
    @DisplayName("POST /api/farmers - Should create farmer successfully")
    void testCreateFarmer_Success() throws Exception {
        // Given
        when(farmerService.createFarmer(any(FarmerDTO.class))).thenReturn(farmerDTO);

        // When & Then
        mockMvc.perform(post("/api/farmers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farmerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(farmerService, times(1)).createFarmer(any(FarmerDTO.class));
    }

    @Test
    @DisplayName("POST /api/farmers - Should return 400 for invalid data")
    void testCreateFarmer_InvalidData() throws Exception {
        // Given
        FarmerDTO invalidFarmer = new FarmerDTO();
        invalidFarmer.setFirstName(""); // Invalid: empty name
        invalidFarmer.setEmail("invalid-email"); // Invalid email format

        // When & Then
        mockMvc.perform(post("/api/farmers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFarmer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/farmers/{id} - Should return farmer by id")
    void testGetFarmerById_Success() throws Exception {
        // Given
        when(farmerService.getFarmerById(1L)).thenReturn(farmerDTO);

        // When & Then
        mockMvc.perform(get("/api/farmers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(farmerService, times(1)).getFarmerById(1L);
    }

    @Test
    @DisplayName("GET /api/farmers/{id} - Should return 404 when farmer not found")
    void testGetFarmerById_NotFound() throws Exception {
        // Given
        when(farmerService.getFarmerById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Farmer not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/farmers/999"))
                .andExpect(status().isNotFound());

        verify(farmerService, times(1)).getFarmerById(999L);
    }

    @Test
    @DisplayName("GET /api/farmers - Should return paginated farmers")
    void testGetAllFarmers_WithPagination() throws Exception {
        // Given
        List<FarmerDTO> farmers = Arrays.asList(farmerDTO, farmerDTO);
        Page<FarmerDTO> farmerPage = new PageImpl<>(farmers, PageRequest.of(0, 10), 2);

        when(farmerService.getAllFarmers(any(PageRequest.class))).thenReturn(farmerPage);

        // When & Then
        mockMvc.perform(get("/api/farmers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].firstName").value("John"));

        verify(farmerService, times(1)).getAllFarmers(any(PageRequest.class));
    }

    @Test
    @DisplayName("PUT /api/farmers/{id} - Should update farmer successfully")
    void testUpdateFarmer_Success() throws Exception {
        // Given
        FarmerDTO updateDTO = new FarmerDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Smith");
        updateDTO.setEmail("jane.smith@example.com");

        when(farmerService.updateFarmer(eq(1L), any(FarmerDTO.class))).thenReturn(updateDTO);

        // When & Then
        mockMvc.perform(put("/api/farmers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));

        verify(farmerService, times(1)).updateFarmer(eq(1L), any(FarmerDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/farmers/{id} - Should delete farmer successfully")
    void testDeleteFarmer_Success() throws Exception {
        // Given
        doNothing().when(farmerService).deleteFarmer(1L);

        // When & Then
        mockMvc.perform(delete("/api/farmers/1"))
                .andExpect(status().isNoContent());

        verify(farmerService, times(1)).deleteFarmer(1L);
    }

    @Test
    @DisplayName("GET /api/farmers/search - Should search farmers by name")
    void testSearchFarmers() throws Exception {
        // Given
        List<FarmerDTO> farmers = Arrays.asList(farmerDTO);
        when(farmerService.searchByName("John")).thenReturn(farmers);

        // When & Then
        mockMvc.perform(get("/api/farmers/search")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(farmerService, times(1)).searchByName("John");
    }

    @Test
    @DisplayName("GET /api/farmers/active - Should return active farmers only")
    void testGetActiveFarmers() throws Exception {
        // Given
        List<FarmerDTO> activeFarmers = Arrays.asList(farmerDTO);
        when(farmerService.findByActiveStatus(true)).thenReturn(activeFarmers);

        // When & Then
        mockMvc.perform(get("/api/farmers/active")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(farmerService, times(1)).findByActiveStatus(true);
    }
}
