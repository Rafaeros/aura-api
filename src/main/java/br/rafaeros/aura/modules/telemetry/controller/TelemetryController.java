package br.rafaeros.aura.modules.telemetry.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.modules.telemetry.controller.dto.TelemetryDTO;
import br.rafaeros.aura.modules.telemetry.service.DeviceTelemetryService;
import br.rafaeros.aura.modules.user.model.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/devices")
public class TelemetryController {

    private final DeviceTelemetryService deviceTelemetryService;

    public TelemetryController(DeviceTelemetryService deviceTelemetryService) {
        this.deviceTelemetryService = deviceTelemetryService;
    }

    @PostMapping("/telemetry")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TelemetryDTO.Response>> ingestTelemetry(
            @Valid @RequestBody TelemetryDTO.CreateRequest request) {
        TelemetryDTO.Response saved = deviceTelemetryService.ingest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Telemetria registrado com sucesso.", saved));
    }

    @GetMapping("/{deviceId}/telemetry")
    @PreAuthorize("@telemetrySecurity.canViewTelemetry(#deviceId, #user)")
    public ResponseEntity<ApiResponse<Page<TelemetryDTO.Response>>> getHistory(
            @PathVariable Long deviceId,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponse.success("Telemetria listada com sucesso.",
                deviceTelemetryService.listHistory(deviceId, pageable)));
    }
}