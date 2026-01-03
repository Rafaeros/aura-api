package br.rafaeros.aura.modules.company.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanySettingsDTO(
        @NotBlank(message = "Everynet token is required") 
        @JsonProperty("everynet_access_token")
        String everynetAccessToken,

        @NotBlank(message = "MQTT Host is required") 
        @JsonProperty("mqtt_host")
        String mqttHost,

        @NotNull(message = "MQTT Port is required")
        @JsonProperty("mqtt_port")
        Integer mqttPort,

        @NotBlank(message = "MQTT Username is required") 
        @JsonProperty("mqtt_username")
        String mqttUsername,

        @NotBlank(message = "MQTT Password is required") 
        @JsonProperty("mqtt_password")
        String mqttPassword) {
}
