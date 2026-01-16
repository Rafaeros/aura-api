package br.rafaeros.aura.modules.auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponseDTO(

        @JsonProperty("token")
        String token,

        @JsonProperty("is_settings_configured")
        boolean isSettingsConfigured,
        
        @JsonProperty("is_first_access")
        boolean isFirstAccess
) {
}