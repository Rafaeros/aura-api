package br.rafaeros.aura.modules.user.controller.dto;

import br.rafaeros.aura.modules.user.model.User;

public record UserResponseDTO(
        Long id,
        String username,
        String email) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
