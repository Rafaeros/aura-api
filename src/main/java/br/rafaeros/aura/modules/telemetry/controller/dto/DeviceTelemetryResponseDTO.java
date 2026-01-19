package br.rafaeros.aura.modules.telemetry.controller.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonRawValue;

import br.rafaeros.aura.modules.telemetry.model.DeviceTelemetry;
import br.rafaeros.aura.modules.telemetry.model.enums.TelemetrySource;

public record DeviceTelemetryResponseDTO(
        Long id,
        Instant createdAt,
        TelemetrySource source,
        String type,
        
        @JsonRawValue
        String payload,
        
        @JsonRawValue
        String metadata
) {

    public static DeviceTelemetryResponseDTO fromEntity(DeviceTelemetry entity) {
        return new DeviceTelemetryResponseDTO(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getSource(),
                entity.getType(),
                entity.getPayload(),
                entity.getMetadata()
        );
    }
}