package br.rafaeros.aura.modules.auth.controller.dto;

public record AuthResponseDTO(
        String token,
        boolean isSettingsConfigured,
        boolean isFirstAccess
) {
}