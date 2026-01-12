package br.rafaeros.aura.modules.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FirstAccessRequest(
        @NotBlank(message = "Email is required") String email,

        @NotBlank(message = "Temporary password is required") 
        @JsonProperty("temp_password")
        String tempPassword,

        @Size(min = 6, message = "Temporary password must be at least 6 characters long")
        @JsonProperty("new_password")
        @NotBlank(message = "New password is required") String newPassword,

        @Size(min = 6, message = "Temporary password must be at least 6 characters long")
        @JsonProperty("confirm_new_password")
        @NotBlank(message = "Confirm new password is required") String confirmNewPassword
) {
}