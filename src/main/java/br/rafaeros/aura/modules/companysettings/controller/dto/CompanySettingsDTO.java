package br.rafaeros.aura.modules.companysettings.controller.dto;

import br.rafaeros.aura.modules.companysettings.model.CompanySettings;
import br.rafaeros.aura.modules.user.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CompanySettingsDTO {

    public record CreateRequest(
            @NotBlank(message = "A chave de acesso da Everynet é obrigatória.") String everynetAccessToken,

            @NotBlank(message = "O host MQTT é obrigatório.") String mqttHost,

            @NotNull(message = "A porta MQTT é obrigatória.") Integer mqttPort,

            @NotBlank(message = "O usuário MQTT é obrigatório.") String mqttUsername,

            @NotBlank(message = "A senha MQTT é obrigatória.") String mqttPassword,

            @NotBlank(message = "O tópico de inscrição MQTT é obrigatório.") String subscribeTopic,

            @NotBlank(message = "O tópico de publicação MQTT é obrigatório.") String publishTopic) {
    }

    public record Response(
            String everynetAccessToken,
            String mqttHost,
            Integer mqttPort,
            String mqttUsername,
            String mqttPassword,
            String subscribeTopic,
            String publishTopic) {
        public static Response fromEntity(CompanySettings settings, Role userRole) {
            boolean canViewSecrets = (userRole == Role.OWNER || userRole == Role.ADMIN);
            return new Response(
                    canViewSecrets ? settings.getEverynetAccessToken() : null,
                    settings.getMqttHost(),
                    settings.getMqttPort(),
                    settings.getMqttUsername(),
                    canViewSecrets ? settings.getMqttPassword() : null,
                    settings.getSubscribeTopic(),
                    settings.getPublishTopic());
        }
    }

    public record UpdateRequest(
            String everynetAccessToken,
            String mqttHost,
            Integer mqttPort,
            String mqttUsername,
            String mqttPassword,
            String subscribeTopic,
            String publishTopic) {
    }

    public record MqttConnectionCredentials(
            String mqttHost,
            Integer mqttPort,
            String mqttUsername,
            String mqttPassword,
            String subscribeTopic,
            String publishTopic) {
        public static MqttConnectionCredentials fromEntity(CompanySettings settings) {
            return new MqttConnectionCredentials(
                    settings.getMqttHost(),
                    settings.getMqttPort(),
                    settings.getMqttUsername(),
                    settings.getMqttPassword(),
                    settings.getSubscribeTopic(),
                    settings.getPublishTopic());
        }
    }
}
