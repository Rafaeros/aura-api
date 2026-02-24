package br.rafaeros.aura.modules.auth.controller.dto;

import java.util.List;

import br.rafaeros.aura.modules.auth.model.ApiKey;

public class ApiDTO {

    public record CreateApiKeyRequest(String description, List<String> authorities, Long companyId) {
    }

    public record ApiKeyResponse(String key, String description) {
        public static ApiKeyResponse fromEntity(ApiKey apiKey) {
            return new ApiKeyResponse(apiKey.getKey(), apiKey.getDescription());
        }
    }
}
