package br.rafaeros.aura.modules.user.controller.dto;

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
        String everynetAccessToken,
        String mqttHost,
        Integer mqttPort,
        String mqttUsername) {
    public static CompanySettingsDTO fromEntity(CompanySettings settings) {
        return new CompanySettingsDTO(
                settings.getId(),
                settings.getEverynetAccessToken(),
                settings.getMqttHost(),
                settings.getMqttPort(),
                settings.getMqttUsername());
    }
}
