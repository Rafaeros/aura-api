package br.rafaeros.aura.modules.companysettings.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.modules.companysettings.controller.dto.CompanySettingsDTO;
import br.rafaeros.aura.modules.companysettings.service.CompanySettingsService;
import br.rafaeros.aura.modules.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanySettingsController {

    private final CompanySettingsService companySettingsService;

    @PostMapping("/{companyId}/settings")
    @PreAuthorize("@companySettingsSecurity.canEditSettings(#companyId, #user)")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.Response>> createSettings(@PathVariable Long companyId,
            @Valid @RequestBody CompanySettingsDTO.CreateRequest request, @AuthenticationPrincipal User user) {
        CompanySettingsDTO.Response settings = companySettingsService.create(companyId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Configurações criadas com sucesso.", settings));
    }

    @PostMapping(value = {"/me/settings", "/current/settings"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.Response>> createMySettings(
            @Valid @RequestBody CompanySettingsDTO.CreateRequest request, @AuthenticationPrincipal User user) {
        if (user.getCompany() == null) {
            throw new br.rafaeros.aura.core.exception.ResourceNotFoundException("Usuário não possui empresa vinculada");
        }
        return createSettings(user.getCompany().getId(), request, user);
    }

    @GetMapping("/{companyId}/settings")
    @PreAuthorize("@companySettingsSecurity.canViewGeneralSettings(#companyId, #user)")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.Response>> getSettings(@PathVariable Long companyId,
            @AuthenticationPrincipal User user) {
        CompanySettingsDTO.Response settings = companySettingsService.findByCompanyId(companyId, user);
        return ResponseEntity.ok(ApiResponse.success("Configurações listadas com sucesso.", settings));
    }

    @GetMapping(value = {"/me/settings", "/current/settings"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.Response>> getMySettings(@AuthenticationPrincipal User user) {
        if (user.getCompany() == null) {
            throw new br.rafaeros.aura.core.exception.ResourceNotFoundException("Usuário não possui empresa vinculada");
        }
        return getSettings(user.getCompany().getId(), user);
    }

    @GetMapping("/{companyId}/settings/mqtt")
    @PreAuthorize("@companySettingsSecurity.canConnectMQTT(#companyId, #user)")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.MqttConnectionCredentials>> getMqttSettings(
            @PathVariable Long companyId,
            @AuthenticationPrincipal User user) {
        CompanySettingsDTO.MqttConnectionCredentials settings = companySettingsService
                .findMqttSettingsByCompanyId(companyId);
        return ResponseEntity.ok(ApiResponse.success("Configurações listadas com sucesso.", settings));
    }

    @GetMapping(value = {"/me/settings/mqtt", "/current/settings/mqtt"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.MqttConnectionCredentials>> getMyMqttSettings(@AuthenticationPrincipal User user) {
        if (user.getCompany() == null) {
            throw new br.rafaeros.aura.core.exception.ResourceNotFoundException("Usuário não possui empresa vinculada");
        }
        return getMqttSettings(user.getCompany().getId(), user);
    }

    @PutMapping("/{companyId}/settings")
    @PreAuthorize("@companySettingsSecurity.canEditSettings(#companyId, #user)")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.Response>> updateSettings(@PathVariable Long companyId,
            @Valid @RequestBody CompanySettingsDTO.UpdateRequest request, @AuthenticationPrincipal User user) {
        CompanySettingsDTO.Response settings = companySettingsService.update(companyId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Configurações atualizadas com sucesso.", settings));
    }

    @PutMapping(value = {"/me/settings", "/current/settings"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.Response>> updateMySettings(
            @Valid @RequestBody CompanySettingsDTO.UpdateRequest request, @AuthenticationPrincipal User user) {
        if (user.getCompany() == null) {
            throw new br.rafaeros.aura.core.exception.ResourceNotFoundException("Usuário não possui empresa vinculada");
        }
        return updateSettings(user.getCompany().getId(), request, user);
    }
}