package br.rafaeros.aura.modules.company.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsDTO;
import br.rafaeros.aura.modules.company.model.CompanySettings;
import br.rafaeros.aura.modules.company.service.CompanySettingsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/company")
public class CompanySettingsController {

    private final CompanySettingsService service;

    public CompanySettingsController(CompanySettingsService service) {
        this.service = service;
    }

    @GetMapping("/{companyId}/settings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompanySettings> get(@PathVariable Long companyId, Authentication auth) {
        return ResponseEntity.ok(service.findByCompanyId(companyId, auth.getName()));
    }

    @PostMapping("/{companyId}/settings")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<CompanySettings> updateSettings(
            @PathVariable Long companyId,
            @Valid @RequestBody CompanySettingsDTO dto, Authentication auth) {

        CompanySettings saved = service.updateByCompanyId(companyId, dto, auth.getName());
        return ResponseEntity.ok(saved);
    }
}