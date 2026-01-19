package br.rafaeros.aura.modules.telemetry.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import br.rafaeros.aura.modules.telemetry.model.enums.TelemetrySource;

public record DeviceTelemetryRequestDTO(
    @NotBlank(message = "Device EUI is required")
    @JsonProperty("dev_eui")
    String devEui,

    @NotNull(message = "Source is required")
    TelemetrySource source,

    @NotBlank(message = "Type is required")
    String type,

    Object payload, 
    
    Object metadata
) {}