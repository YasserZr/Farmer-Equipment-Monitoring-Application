package com.farm.equipment.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import feign.FeignException;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@DisplayName("Farmers Feign Client Integration Tests")
class FarmerClientTest {

    @Autowired
    private FarmersFeignClient farmersFeignClient;

    @Test
    @DisplayName("Should verify farmer exists successfully")
    void testFarmerExists_Success() {
        // Given
        UUID farmerId = UUID.randomUUID();

        stubFor(get(urlEqualTo("/api/farmers/" + farmerId + "/exists"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        // When
        Boolean exists = farmersFeignClient.farmerExists(farmerId);

        // Then
        assertThat(exists).isTrue();

        verify(getRequestedFor(urlEqualTo("/api/farmers/" + farmerId + "/exists")));
    }

    @Test
    @DisplayName("Should handle farmer not found")
    void testFarmerExists_NotFound() {
        // Given
        UUID farmerId = UUID.randomUUID();

        stubFor(get(urlEqualTo("/api/farmers/" + farmerId + "/exists"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Farmer not found\"}")));

        // When & Then
        assertThatThrownBy(() -> farmersFeignClient.farmerExists(farmerId))
                .isInstanceOf(FeignException.class);

        verify(getRequestedFor(urlEqualTo("/api/farmers/" + farmerId + "/exists")));
    }

    @Test
    @DisplayName("Should verify permission check")
    void testCheckPermission_Success() {
        // Given
        UUID farmerId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        String responseBody = """
                {
                    "allowed": true,
                    "farmerId": "" + farmerId + "",
                    "resourceId": "" + resourceId + ""
                }
                """;

        stubFor(get(urlMatching("/api/farmers/" + farmerId + "/permissions.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        // When
        PermissionCheckResponse response = farmersFeignClient.checkPermission(farmerId, resourceId, "READ");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isAllowed()).isTrue();
    }
}
