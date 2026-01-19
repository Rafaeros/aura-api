package br.rafaeros.aura.modules.telemetry.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryRequestDTO;
import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryResponseDTO;
import br.rafaeros.aura.modules.telemetry.service.DeviceTelemetryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/telemetry")
public class DeviceTelemetryController {

    private final DeviceTelemetryService deviceTelemetryService;

    public DeviceTelemetryController(DeviceTelemetryService deviceTelemetryService) {
        this.deviceTelemetryService = deviceTelemetryService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeviceTelemetryResponseDTO> create(@Valid @RequestBody DeviceTelemetryRequestDTO request) {
        DeviceTelemetryResponseDTO saved = deviceTelemetryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

}