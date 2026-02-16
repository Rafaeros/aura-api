package br.rafaeros.aura.modules.user.controller.dto;

import br.rafaeros.aura.modules.company.controller.dto.CompanyDTO;
import br.rafaeros.aura.modules.companysettings.controller.dto.CompanySettingsDTO;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDTO {
        ;
        public record CreateRequest(
                        @NotBlank(message = "O Primeiro Nome é obrigatório.") String firstName,

                        @NotBlank(message = "O Sobrenome é obrigatório.") String lastName,

                        @NotBlank(message = "Nome de usuário é obrigatório.") @Size(min = 3, max = 20, message = "Nome de usuário deve ter entre 3 e 20 caracteres.") String username,

                        @NotBlank(message = "Email é obrigatório.") @Email(message = "Formato de email inválido.") @Size(max = 50, message = "Email deve ter no máximo 50 caracteres.") String email,

                        @NotNull(message = "Função é obrigatória.") Role role,

                        @NotNull(message = "O ID da empresa é obrigatório.") Long companyId) {
        }

        public record Response(
                        Long id,
                        String firstName,
                        String lastName,
                        String username,
                        String email,
                        Role role,
                        Long companyId) {
                public static Response fromEntity(User user) {
                        return new Response(
                                        user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(),
                                        user.getEmail(), user.getRole(),
                                        user.getCompany() != null ? user.getCompany().getId() : null);
                }
        }

        public record ProfileResponse(
                        Long id,
                        String firstName,
                        String lastName,
                        String username,
                        String email,
                        CompanyDTO.Response company,
                        CompanySettingsDTO.Response settings) {
                public static ProfileResponse fromEntity(User user) {

                        var settingsDTO = (user.getCompany().getSettings() != null)
                                        ? CompanySettingsDTO.Response.fromEntity(user.getCompany().getSettings(),
                                                        user.getRole())
                                        : null;
                        return new ProfileResponse(
                                        user.getId(),
                                        user.getFirstName(),
                                        user.getLastName(),
                                        user.getUsername(),
                                        user.getEmail(),
                                        CompanyDTO.Response.fromEntity(user.getCompany()),
                                        settingsDTO);
                }
        }

        public record UpdateRequest(
                        String firstName,
                        String lastName,
                        String username,
                        String email,
                        Role role,
                        Long companyId) {
        }

        public record ChangePasswordRequest(
                        @NotBlank(message = "A senha é obrigatória.") String currentPassword,

                        @NotBlank(message = "A nova senha é obrigatória.") @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.") String newPassword,

                        @NotBlank(message = "A confirmação da nova senha é obrigatória.") @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.") String confirmPassword) {
        }

}
