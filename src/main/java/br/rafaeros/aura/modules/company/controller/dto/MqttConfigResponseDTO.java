package br.rafaeros.aura.modules.company.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.rafaeros.aura.modules.company.model.CompanySettings;

public record MqttConfigResponseDTO(
        @JsonProperty("mqtt_host")
        String mqttHost,
        @JsonProperty("mqtt_port")
        Integer mqttPort,
        @JsonProperty("mqtt_username")
        String mqttUsername,
        @JsonProperty("mqtt_password")
        String mqttPassword,
        @JsonProperty("subscribe_topic")
        String subscribeTopic,
        @JsonProperty("publish_topic")
        String publishTopic) {
            
    public static MqttConfigResponseDTO fromCompanySettingsEntity(CompanySettings settings) {
        return new MqttConfigResponseDTO(
                settings.getMqttHost(),
                settings.getMqttPort(),
                settings.getMqttUsername(),
                settings.getMqttPassword(),
                settings.getSubscribeTopic(),
                settings.getPublishTopic());
    }
}
