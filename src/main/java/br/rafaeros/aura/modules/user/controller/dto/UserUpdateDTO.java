package br.rafaeros.aura.modules.user.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(
    @NotBlank(message = "The username is required")
    String username,

    @NotBlank(message = "The password is required")
    String password 
){}
