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

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsRequestDTO;
import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsResponseDTO;
import br.rafaeros.aura.modules.company.service.CompanySettingsService;
import br.rafaeros.aura.modules.user.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/companies")
public class CompanySettingsController {

    private final CompanySettingsService service;
    private final UserService userService;

    public CompanySettingsController(CompanySettingsService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    // User
    @GetMapping("/current/settings")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<CompanySettingsResponseDTO> getMySettings(Authentication auth) {
        CompanySettingsResponseDTO settings = service.findMyCompanySettings(auth.getName());
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/current/settings")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<CompanySettingsResponseDTO> updateMySettings(
            @Valid @RequestBody CompanySettingsRequestDTO dto, Authentication auth) {

        var user = userService.findByEmail(auth.getName());

        if (user.getCompany() == null) {
            throw new BusinessException("User is not associated with a company.");
        }

        CompanySettingsResponseDTO saved = service.updateByCompanyId(user.getCompany().getId(), dto, auth.getName());
        return ResponseEntity.ok(saved);
    }

    // Admin
    @GetMapping("/{companyId}/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanySettingsResponseDTO> get(@PathVariable Long companyId, Authentication auth) {
        return ResponseEntity.ok(service.findByCompanyId(companyId));
    }

    @PostMapping("/{companyId}/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanySettingsResponseDTO> updateSettings(
            @PathVariable Long companyId,
            @Valid @RequestBody CompanySettingsRequestDTO dto, Authentication auth) {

        CompanySettingsResponseDTO saved = service.updateByCompanyId(companyId, dto, auth.getName());
        return ResponseEntity.ok(saved);
    }
}