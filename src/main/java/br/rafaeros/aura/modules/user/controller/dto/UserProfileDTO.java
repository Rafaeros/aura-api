package br.rafaeros.aura.modules.user.controller.dto;

import br.rafaeros.aura.modules.company.controller.dto.CompanyResponseDTO;
import br.rafaeros.aura.modules.user.model.User;

public record UserProfileDTO(
        Long id,
        String username,
        String email,
        CompanyResponseDTO company
    ) {
    public static UserProfileDTO fromEntity(User user) {
        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                CompanyResponseDTO.fromEntity(user.getCompany()));
    }
}
