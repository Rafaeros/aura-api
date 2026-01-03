package br.rafaeros.aura.modules.user.controller.dto;

import br.rafaeros.aura.modules.user.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record UserCreateDTO(
        String username,
        String password,
        Role role,
        @NotNull(message = "The company ID is required") @JsonProperty("company_id")
                Long companyId) {}
