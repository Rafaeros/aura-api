package br.rafaeros.aura.modules.auth.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.modules.auth.controller.dto.ApiDTO;
import br.rafaeros.aura.modules.auth.model.ApiKey;
import br.rafaeros.aura.modules.auth.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public ApiDTO.ApiKeyResponse createApiKey(String description, List<String> authorities) {
        ApiKey apiKey = new ApiKey();
        apiKey.setKey(generateSecureKey());
        apiKey.setDescription(description);
        String authoritiesString = String.join(",", authorities);
        apiKey.setAuthorities(authoritiesString);
        ApiKey saved = apiKeyRepository.save(apiKey);
        return ApiDTO.ApiKeyResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteApiKey(Long id) {
        apiKeyRepository.deleteById(Objects.requireNonNull(id));
    }

    private String generateSecureKey() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return "aura_live_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}