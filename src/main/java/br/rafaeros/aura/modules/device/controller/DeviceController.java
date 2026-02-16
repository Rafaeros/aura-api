package br.rafaeros.aura.modules.device.controller;

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
import br.rafaeros.aura.modules.device.controller.dto.DeviceDTO;
import br.rafaeros.aura.modules.device.service.DeviceService;
import br.rafaeros.aura.modules.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
    
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DeviceDTO.Response>> create(
            @RequestBody @Valid DeviceDTO.RegisterRequest request, @AuthenticationPrincipal User user) {
        DeviceDTO.Response savedDevice = deviceService.createDevice(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Dispositivo registrado com sucesso.", savedDevice));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<DeviceDTO.Response>>> getAll(
            @AuthenticationPrincipal User user,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Dispositivos listados com sucesso.", deviceService.findAllUserDevices(user, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@deviceSecurity.canViewDevice(#id, #user)")
    public ResponseEntity<ApiResponse<DeviceDTO.DetailsResponse>> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        DeviceDTO.DetailsResponse device = deviceService.findById(id, user);
        return ResponseEntity.ok(ApiResponse.success("Dispositivo encontrado com sucesso.", device));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("@deviceSecurity.canUnlinkDevice(#id, #user)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        deviceService.unlinkDevice(id, user);
        return ResponseEntity.ok(ApiResponse.success("Dispositivo desvinculado com sucesso."));
    }
}