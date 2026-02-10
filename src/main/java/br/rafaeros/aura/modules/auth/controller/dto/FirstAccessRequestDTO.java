package br.rafaeros.aura.modules.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FirstAccessRequestDTO(
        @NotBlank(message = "A senha temporária é obrigatória.")
        String tempPassword,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.")
        String newPassword,

        @NotBlank(message = "A confirmação da nova senha é obrigatória.")
        @Size(min = 6, message = "A confirmação da senha deve ter no mínimo 6 caracteres.")
        String confirmNewPassword
) {}