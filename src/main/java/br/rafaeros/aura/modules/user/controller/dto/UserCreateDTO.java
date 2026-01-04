package br.rafaeros.aura.modules.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.rafaeros.aura.modules.user.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateDTO(
        @NotBlank(message = "Username (display name) is required")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 50, message = "Email must be at most 50 characters long")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 50, message = "Password must be at least 6 characters long")
        String password,

        @NotNull(message = "Role is required")
        Role role,

        @NotNull(message = "The company ID is required")
        @JsonProperty("company_id")
        Long companyId
) {}