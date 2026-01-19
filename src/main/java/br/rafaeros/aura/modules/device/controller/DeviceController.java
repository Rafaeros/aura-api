package br.rafaeros.aura.modules.device.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.modules.device.controller.dto.DeviceCreateRequestDTO;
import br.rafaeros.aura.modules.device.controller.dto.DeviceDetailsResponseDTO;
import br.rafaeros.aura.modules.device.controller.dto.DeviceListResponseDTO;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.service.DeviceService;
import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryResponseDTO;
import br.rafaeros.aura.modules.telemetry.service.DeviceTelemetryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceTelemetryService telemetryService;

    public DeviceController(DeviceService deviceService, DeviceTelemetryService telemetryService) {
        this.deviceService = deviceService;
        this.telemetryService = telemetryService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OWNER')")
    public ResponseEntity<Device> create(
            @RequestBody @Valid DeviceCreateRequestDTO dto, Authentication authentication) {

        Device savedDevice = deviceService.createDevice(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<DeviceListResponseDTO>> getAll(
            Authentication authentication,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(deviceService.listDevicesSmart(authentication.getName(), pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeviceDetailsResponseDTO> getById(@PathVariable Long id, Authentication authentication) {
        DeviceDetailsResponseDTO device = deviceService.findById(id, authentication.getName());
        return ResponseEntity.ok(device);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        deviceService.unlinkDevice(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/telemetry")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<DeviceTelemetryResponseDTO>> getDeviceTelemetryHistory(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(telemetryService.listHistory(id, pageable));
    }
}