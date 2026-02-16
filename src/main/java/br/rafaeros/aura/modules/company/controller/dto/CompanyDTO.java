package br.rafaeros.aura.modules.company.controller.dto;

import br.rafaeros.aura.modules.company.model.Company;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public enum CompanyDTO {;

    public record CreateRequest (
            @NotBlank(message = "O nome da empresa é obrigatório.")
            String name,

            @NotBlank(message = "O CNPJ é obrigatório.")
            @Size(min = 14, max = 14, message = "O CNPJ deve ter exatos 14 dígitos.")
            @Pattern(regexp = "\\d+", message = "O CNPJ precisa conter apenas números.")
            String cnpj,

            String cep,
            Integer addressNumber
    ) {}

    public record UpdateRequest (
            @NotBlank(message = "O nome da empresa é obrigatório.")
            String name,

            @NotBlank(message = "O CNPJ é obrigatório.")
            @Size(min = 14, max = 14, message = "O CNPJ deve ter exatos 14 dígitos.")
            @Pattern(regexp = "\\d+", message = "O CNPJ precisa conter apenas números.")
            String cnpj,
            String cep,
            Integer addressNumber
    ) {}

    public record Response (
            Long id,
            String name,
            String cnpj,
            String cep,
            Integer addressNumber,
            boolean isActive
    ) {
        public static Response fromEntity(Company company) {
            return new Response(
                    company.getId(),
                    company.getName(),
                    company.getCnpj(),
                    company.getCep(),
                    company.getAddressNumber(),
                    company.isActive()
            );
        }
    }

}
