package br.rafaeros.aura.core.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiKeyPrincipal {
    private final Long apiKeyId;
    private final Long companyId;
    private final String description;
}