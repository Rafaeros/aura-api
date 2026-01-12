package br.rafaeros.aura.modules.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(

        @JsonProperty("token")
        String token,

        @JsonProperty("username")
        String username,

        @JsonProperty("email")
        String email,

        @JsonProperty("role")
        String role,

        @JsonProperty("is_settings_configured")
        boolean isSettingsConfigured,
        
        @JsonProperty("is_first_access")
        boolean isFirstAccess
) {
}