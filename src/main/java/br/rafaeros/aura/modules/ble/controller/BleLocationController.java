package br.rafaeros.aura.modules.ble.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import necessário
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // Import necessário

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.core.security.ApiKeyPrincipal;
import br.rafaeros.aura.modules.ble.controller.dto.BleDeviceDTO;
import br.rafaeros.aura.modules.ble.service.BleLocationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ble")
@RequiredArgsConstructor
public class BleLocationController {

    private final BleLocationService bleLocationService;

    @GetMapping("/{deviceId}/locations")
    @PreAuthorize("hasAuthority('BLE:READ')")
    public ResponseEntity<ApiResponse<Page<BleDeviceDTO.Location>>> getDeviceLocations(
            @PathVariable Long deviceId,
            @AuthenticationPrincipal ApiKeyPrincipal principal,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        
        return ResponseEntity.ok(ApiResponse.success(
                "Localizações do dispositivo listadas com sucesso.",
                bleLocationService.findLocationsByDevice(deviceId, principal.getCompanyId(), pageable)));
    }
}