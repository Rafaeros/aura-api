package br.rafaeros.aura.modules.jiga.controller;

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.core.security.ApiKeyPrincipal;
import br.rafaeros.aura.modules.jiga.controller.dto.JigaProvisioningDTO;
import br.rafaeros.aura.modules.jiga.service.JigaProvisioningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jiga")
@RequiredArgsConstructor
public class JigaController {

    private final JigaProvisioningService jigaService;

    @PostMapping("/provision")
    @PreAuthorize("hasAuthority('JIGA:WRITE')")
    public ResponseEntity<ApiResponse<Void>> provisionarDispositivo(
            @Valid @RequestBody JigaProvisioningDTO request,
            @AuthenticationPrincipal ApiKeyPrincipal principal) {
        
        // O principal.getCompanyId() fornece o TenantID de forma segura
        jigaService.processJigaData(request, principal.getCompanyId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dados da Jiga salvos com sucesso na AWS.", null));
    }
}