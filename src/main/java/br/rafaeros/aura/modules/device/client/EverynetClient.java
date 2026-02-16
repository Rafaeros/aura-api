package br.rafaeros.aura.modules.device.client;

import br.rafaeros.aura.core.config.EverynetProperties;
import br.rafaeros.aura.modules.device.client.dto.EverynetDevice;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class EverynetClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public EverynetClient(EverynetProperties properties, ObjectMapper objectMapper) {
        String baseUrl = Objects.requireNonNull(properties.getBaseUrl(), "URL base do Everynet não pode ser nula");
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public EverynetDevice getDeviceByDevEui(String devEui, String apiToken) {
        try {
            JsonNode rootNode = restClient
                    .get()
                    .uri(
                            uriBuilder -> uriBuilder
                                    .path("/devices/{devEui}")
                                    .queryParam("access_token", apiToken)
                                    .build(devEui))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            (request, response) -> {
                                throw new RuntimeException(
                                        "Dispositivo não encontrado no Everynet com EUI: " + devEui);
                            })
                    .body(JsonNode.class);

            if (rootNode != null && rootNode.has("device")) {
                JsonNode deviceNode = rootNode.get("device");
                log.info("Dispositivo encontrado no Everynet com EUI: " + devEui);
                log.info("Dados do dispositivo: " + deviceNode);
                return objectMapper.treeToValue(deviceNode, EverynetDevice.class);
            }

            throw new RuntimeException("Resposta do Everynet não contém a chave 'device'");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao se comunicar com Everynet: " + e.getMessage());
        }
    }
}
