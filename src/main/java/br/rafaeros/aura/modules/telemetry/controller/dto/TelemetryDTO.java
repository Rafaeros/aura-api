package br.rafaeros.aura.modules.telemetry.controller.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonRawValue;

import br.rafaeros.aura.modules.telemetry.model.Telemetry;
import br.rafaeros.aura.modules.telemetry.model.enums.TelemetrySource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TelemetryDTO {

    public record CreateRequest(
            @NotBlank(message = "ID do dispositivo (DevEUI) é obrigatório") String devEui,

            @NotNull(message = "Origem é obrigatória") TelemetrySource source,

            @NotBlank(message = "Tipo é obrigatório") String type,

            Object payload) {
    };

    public record Response(
            Long id,

            TelemetrySource source,

            String type,

            @JsonRawValue String payload,

            Instant createdAt) {

        public static Response fromEntity(Telemetry entity) {
            return new Response(
                    entity.getId(),
                    entity.getSource(),
                    entity.getType(),
                    entity.getPayload(),
                    entity.getCreatedAt());
        }
    }
}
