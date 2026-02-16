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
@RequestMapping("/companies/{companyId}/settings")
@RequiredArgsConstructor
public class CompanySettingsController {

    private final CompanySettingsService companySettingsService;

    @PostMapping
    @PreAuthorize("@companySettingsSecurity.canEditSettings(#companyId, #user)")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.Response>> createSettings(@PathVariable Long companyId,
            @Valid @RequestBody CompanySettingsDTO.CreateRequest request, @AuthenticationPrincipal User user) {
        CompanySettingsDTO.Response settings = companySettingsService.create(companyId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Configurações criadas com sucesso.", settings));
    }

    @GetMapping
    @PreAuthorize("@companySettingsSecurity.canViewGeneralSettings(#companyId, #user)")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.Response>> getSettings(@PathVariable Long companyId,
            @AuthenticationPrincipal User user) {
        CompanySettingsDTO.Response settings = companySettingsService.findByCompanyId(companyId, user);
        return ResponseEntity.ok(ApiResponse.success("Configurações listadas com sucesso.", settings));
    }

    @GetMapping("/mqtt")
    @PreAuthorize("@companySettingsSecurity.canConnectMQTT(#companyId, #user)")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.MqttConnectionCredentials>> getMqttSettings(
            @PathVariable Long companyId,
            @AuthenticationPrincipal User user) {
        CompanySettingsDTO.MqttConnectionCredentials settings = companySettingsService
                .findMqttSettingsByCompanyId(companyId);
        return ResponseEntity.ok(ApiResponse.success("Configurações listadas com sucesso.", settings));
    }

    @PutMapping
    @PreAuthorize("@companySettingsSecurity.canEditSettings(#companyId, #user)")
    public ResponseEntity<ApiResponse<CompanySettingsDTO.Response>> updateSettings(@PathVariable Long companyId,
            @Valid @RequestBody CompanySettingsDTO.UpdateRequest request, @AuthenticationPrincipal User user) {
        CompanySettingsDTO.Response settings = companySettingsService.update(companyId, request, user);
        return ResponseEntity.ok(ApiResponse.success("Configurações atualizadas com sucesso.", settings));
    }
}