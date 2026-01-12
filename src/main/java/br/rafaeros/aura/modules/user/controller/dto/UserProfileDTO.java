package br.rafaeros.aura.modules.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.model.CompanySettings;
import br.rafaeros.aura.modules.user.model.User;

public record UserProfileDTO(
        Long id,
        String username,
        String email,
        CompanyProfileDTO company) {
    public static UserProfileDTO fromEntity(User user) {
        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                CompanyProfileDTO.fromEntity(user.getCompany()));
    }
}

record CompanyProfileDTO(
        Long id,
        String name,
        String cnpj,
        String cep,
        CompanySettingsDTO settings) {
    public static CompanyProfileDTO fromEntity(Company company) {
        return new CompanyProfileDTO(
                company.getId(),
                company.getName(),
                company.getCnpj(),
                company.getCep(),
                company.getSettings() != null ? CompanySettingsDTO.fromEntity(company.getSettings()) : null);
    }
}

record CompanySettingsDTO(
        Long id,

        @JsonProperty("everynet_access_token") String everynetAccessToken,

        @JsonProperty("mqtt_host") String mqttHost,

        @JsonProperty("mqtt_port") Integer mqttPort,

        @JsonProperty("mqtt_username") String mqttUsername,

        @JsonProperty("mqtt_password") String mqttPassword) {
    public static CompanySettingsDTO fromEntity(CompanySettings settings) {
        return new CompanySettingsDTO(
                settings.getId(),
                settings.getEverynetAccessToken(),
                settings.getMqttHost(),
                settings.getMqttPort(),
                settings.getMqttUsername(),
                settings.getMqttPassword());
    }
}
