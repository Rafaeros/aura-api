package br.rafaeros.aura.modules.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.modules.auth.controller.dto.ApiDTO;
import br.rafaeros.aura.modules.auth.service.ApiKeyService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api-keys")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApiDTO.ApiKeyResponse>> createApiKey(
            @RequestBody ApiDTO.CreateApiKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Chave de API criada com sucesso.",
                apiKeyService.createApiKey(request.description(), request.authorities())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApiKey(@PathVariable Long id) {
        apiKeyService.deleteApiKey(id);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}