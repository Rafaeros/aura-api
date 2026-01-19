package br.rafaeros.aura.modules.company.controller.dto;

import br.rafaeros.aura.modules.company.model.Company;

public record CompanyResponseDTO(
        Long id,
        String name,
        String cnpj,
        String cep,
        Integer addressNumber,
        CompanySettingsResponseDTO settings) {

    public static CompanyResponseDTO fromEntity(Company company) {
        return new CompanyResponseDTO(
                company.getId(),
                company.getName(),
                company.getCnpj(),
                company.getCep(),
                company.getAddressNumber(),
                company.getSettings() != null ? CompanySettingsResponseDTO.fromEntity(company.getSettings()) : null);
    }
}