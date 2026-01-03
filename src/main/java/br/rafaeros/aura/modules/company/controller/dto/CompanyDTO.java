package br.rafaeros.aura.modules.company.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CompanyDTO(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "CNPJ is required")
    @Size(min = 14, max = 14, message = "CNPJ must have 14 digits")
    @Pattern(regexp = "\\d+", message = "CNPJ must contain only numbers")
    String cnpj,

    @Size(min = 8, max = 8, message = "CEP must have 8 digits")
    @Pattern(regexp = "\\d+", message = "CEP must contain only numbers")
    String cep,


    @JsonProperty("address_number")
    Integer addressNumber
) {}