package br.rafaeros.aura.modules.company.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.rafaeros.aura.modules.company.model.CompanySettings;

// ADICIONE 'public' AQUI
public record CompanySettingsResponseDTO(
        Long id,

        @JsonProperty("everynet_access_token") String everynetAccessToken,

        @JsonProperty("mqtt_host") String mqttHost,

        @JsonProperty("mqtt_port") Integer mqttPort,

        @JsonProperty("mqtt_username") String mqttUsername,

        @JsonProperty("mqtt_password") String mqttPassword) {

    public static CompanySettingsResponseDTO fromEntity(CompanySettings settings) {
        return new CompanySettingsResponseDTO(
                settings.getId(),
                settings.getEverynetAccessToken(),
                settings.getMqttHost(),
                settings.getMqttPort(),
                settings.getMqttUsername(),
                settings.getMqttPassword());
    }

    public CompanySettingsResponseDTO maskSecrets() {
        return new CompanySettingsResponseDTO(
                this.id(),
                null,
                this.mqttHost(),
                this.mqttPort(),
                this.mqttUsername(),
                null 
        );
    }
}