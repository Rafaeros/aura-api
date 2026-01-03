package br.rafaeros.aura.modules.company.controller;

import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsDTO;
import br.rafaeros.aura.modules.company.model.CompanySettings;
import br.rafaeros.aura.modules.company.service.CompanySettingsService;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company/settings")
public class CompanySettingsController {

    private final CompanySettingsService service;
    private final UserService userService;

    public CompanySettingsController(CompanySettingsService service, UserService userService) {

        this.service = service;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'USER')")
    public ResponseEntity<CompanySettings> get(Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        return ResponseEntity.ok(service.getByCompanyId(user.getCompany().getId()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<CompanySettings> save(
            @Valid @RequestBody CompanySettingsDTO dto, Authentication auth) {

        CompanySettings saved = service.saveOrUpdate(auth.getName(), dto);
        return ResponseEntity.ok(saved);
    }
}
