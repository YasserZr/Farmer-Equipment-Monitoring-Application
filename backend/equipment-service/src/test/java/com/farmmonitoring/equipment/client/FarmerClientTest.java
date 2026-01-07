package com.farmmonitoring.equipment.client;

import com.farmmonitoring.equipment.dto.FarmerDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@DisplayName("Farmer Client Feign Integration Tests")
class FarmerClientTest {

    @Autowired
    private FarmerClient farmerClient;

    @Test
    @DisplayName("Should fetch farmer by id successfully")
    void testGetFarmerById_Success() {
        // Given
        Long farmerId = 1L;
        String responseBody = """
                {
                    "id": 1,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "phone": "+1234567890",
                    "active": true
                }
                """;

        stubFor(get(urlEqualTo("/api/farmers/" + farmerId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        // When
        FarmerDTO farmer = farmerClient.getFarmerById(farmerId);

        // Then
        assertThat(farmer).isNotNull();
        assertThat(farmer.getId()).isEqualTo(1L);
        assertThat(farmer.getFirstName()).isEqualTo("John");
        assertThat(farmer.getEmail()).isEqualTo("john.doe@example.com");

        verify(getRequestedFor(urlEqualTo("/api/farmers/" + farmerId)));
    }

    @Test
    @DisplayName("Should handle farmer not found")
    void testGetFarmerById_NotFound() {
        // Given
        Long farmerId = 999L;

        stubFor(get(urlEqualTo("/api/farmers/" + farmerId))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Farmer not found\"}")));

        // When & Then
        try {
            farmerClient.getFarmerById(farmerId);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(FeignException.class);
        }

        verify(getRequestedFor(urlEqualTo("/api/farmers/" + farmerId)));
    }

    @Test
    @DisplayName("Should verify farmer exists")
    void testVerifyFarmerExists_Success() {
        // Given
        Long farmerId = 1L;

        stubFor(head(urlEqualTo("/api/farmers/" + farmerId))
                .willReturn(aResponse()
                        .withStatus(200)));

        // When
        boolean exists = farmerClient.farmerExists(farmerId);

        // Then
        assertThat(exists).isTrue();

        verify(headRequestedFor(urlEqualTo("/api/farmers/" + farmerId)));
    }
}
