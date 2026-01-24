package br.rafaeros.aura.modules.telemetry.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryRequestDTO;
import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryResponseDTO;
import br.rafaeros.aura.modules.telemetry.service.DeviceTelemetryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/devices")
public class DeviceTelemetryController {

    private final DeviceTelemetryService deviceTelemetryService;

    public DeviceTelemetryController(DeviceTelemetryService deviceTelemetryService) {
        this.deviceTelemetryService = deviceTelemetryService;
    }

    @PostMapping("/telemetry")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeviceTelemetryResponseDTO> ingestTelemetry(
            @Valid @RequestBody DeviceTelemetryRequestDTO request) {
        DeviceTelemetryResponseDTO saved = deviceTelemetryService.ingest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{deviceId}/telemetry")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<DeviceTelemetryResponseDTO>> getHistory(
            @PathVariable Long deviceId,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(deviceTelemetryService.listHistory(deviceId, pageable));
    }
}