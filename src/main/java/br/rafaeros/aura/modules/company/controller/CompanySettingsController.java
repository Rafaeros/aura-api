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
import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsDTO;
import br.rafaeros.aura.modules.company.model.CompanySettings;
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

    @GetMapping("/current/settings")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<CompanySettings> getMySettings(Authentication auth) {
        CompanySettings settings = service.findMyCompanySettings(auth.getName());

        return ResponseEntity.ok(settings);
    }

    @PostMapping("/current/settings")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<CompanySettings> updateMySettings(
            @Valid @RequestBody CompanySettingsDTO dto, Authentication auth) {

        var user = userService.findByEmail(auth.getName());

        if (user.getCompany() == null) {
            throw new BusinessException("User is not associated with a company.");
        }

        CompanySettings saved = service.updateByCompanyId(user.getCompany().getId(), dto, auth.getName());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{companyId}/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanySettings> get(@PathVariable Long companyId, Authentication auth) {
        return ResponseEntity.ok(service.findByCompanyId(companyId));
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