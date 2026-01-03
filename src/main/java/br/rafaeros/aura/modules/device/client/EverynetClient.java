package br.rafaeros.aura.modules.device.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import br.rafaeros.aura.core.config.EverynetProperties;
import br.rafaeros.aura.modules.device.client.dto.EverynetDevice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EverynetClient {

    private final RestClient restClient;
    private final String apiToken;
    private final ObjectMapper objectMapper;

    public EverynetClient(EverynetProperties properties, ObjectMapper objectMapper) {
        String token = Objects.requireNonNull(properties.getApiToken(), "Everynet API token cannot be null");
        String baseUrl = Objects.requireNonNull(properties.getBaseUrl(), "Everynet base URL cannot be null");
        this.apiToken = token;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public EverynetDevice getDeviceByDevEui(String devEui) {
        try {
            JsonNode rootNode = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/devices/{devEui}")
                            .queryParam("access_token", this.apiToken)
                            .build(devEui))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new RuntimeException("Device not found on Everynet with EUI: " + devEui);
                    })
                    .body(JsonNode.class);

            if (rootNode != null && rootNode.has("device")) {
                JsonNode deviceNode = rootNode.get("device");
                log.info("Device found on Everynet with EUI: " + devEui);
                log.info("Device data: " + deviceNode);
                return objectMapper.treeToValue(deviceNode, EverynetDevice.class);
            }

            throw new RuntimeException("Response from Everynet does not contain 'device' key");

        } catch (Exception e) {
            throw new RuntimeException("Error communicating with Everynet: " + e.getMessage());
        }
    }
}