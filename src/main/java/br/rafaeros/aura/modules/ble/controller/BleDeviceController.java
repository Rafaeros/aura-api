package br.rafaeros.aura.modules.ble.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.core.security.ApiKeyPrincipal;
import br.rafaeros.aura.modules.ble.controller.dto.BleDeviceDTO;
import br.rafaeros.aura.modules.ble.service.BleDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ble")
@RequiredArgsConstructor
public class BleDeviceController {

    private final BleDeviceService bleService;

    @PostMapping
    @PreAuthorize("hasAuthority('BLE:WRITE')")
    public ResponseEntity<ApiResponse<BleDeviceDTO.Response>> create(
            @Valid @RequestBody BleDeviceDTO.CreateRequest request,
            @AuthenticationPrincipal ApiKeyPrincipal principal) {
        BleDeviceDTO.Response response = bleService.create(request, principal.getCompanyId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dispositivo registrado com sucesso.", response));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BLE:READ')")
    public ResponseEntity<ApiResponse<Page<BleDeviceDTO.Response>>> getAll(
            @AuthenticationPrincipal ApiKeyPrincipal principal,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Dispositivos listados com sucesso.",
                bleService.findAllDevicesByCompanyId(principal.getCompanyId(), pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BLE:READ')")
    public ResponseEntity<ApiResponse<BleDeviceDTO.DetailsResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal ApiKeyPrincipal principal) {

        BleDeviceDTO.DetailsResponse response = bleService.findById(id, principal.getCompanyId());

        return ResponseEntity.ok(ApiResponse.success("Dispositivo listado com sucesso.", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BLE:WRITE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
            @AuthenticationPrincipal ApiKeyPrincipal principal) {
        bleService.delete(id, principal.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success("Dispositivo deletado com sucesso."));
    }
}
