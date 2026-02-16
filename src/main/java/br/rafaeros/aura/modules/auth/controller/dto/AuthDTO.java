package br.rafaeros.aura.modules.auth.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDTO {

    public record Request(
            @NotBlank(message = "O e-mail é obrigatório.") @Email(message = "O formato do e-mail é inválido.") String email,
            @NotBlank(message = "A senha é obrigatória.") String password) {
    }

    public record FirstAccessRequest(@NotBlank(message = "A senha temporária é obrigatória.") String tempPassword,

            @NotBlank(message = "A nova senha é obrigatória.") @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.") String newPassword,

            @NotBlank(message = "A confirmação da nova senha é obrigatória.") @Size(min = 6, message = "A confirmação da senha deve ter no mínimo 6 caracteres.") String confirmNewPassword) {
    }

    public record Response(String token, boolean isSettingsConfigured, boolean isFirstAccess) {}
}
