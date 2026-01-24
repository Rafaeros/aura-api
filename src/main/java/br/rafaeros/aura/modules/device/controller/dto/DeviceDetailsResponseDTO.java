package br.rafaeros.aura.modules.device.controller.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.model.DeviceFeature;
import br.rafaeros.aura.modules.device.model.DevicePosition;
import br.rafaeros.aura.modules.device.model.DeviceTag;
import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryResponseDTO;

public record DeviceDetailsResponseDTO(
        Long id,

        String name,

        @JsonProperty("dev_eui") String devEui,

        @JsonProperty("dev_addr") String devAddr,

        @JsonProperty("app_eui") String appEui,

        @JsonProperty("nwks_key") String nwksKey,

        @JsonProperty("apps_key") String appsKey,

        List<DeviceTag> tags,

        @JsonProperty("recent_positions") List<DevicePosition> recentPositions,
        List<DeviceFeature> features,

        @JsonProperty("recent_logs") List<DeviceTelemetryResponseDTO> recentLogs
    
    ) {
    public static DeviceDetailsResponseDTO fromEntity(String customName, Device device,
            List<DevicePosition> recentPositions, List<DeviceTelemetryResponseDTO> recentLogs) {
        return new DeviceDetailsResponseDTO(
                device.getId(),
                customName,
                device.getDevEui(),
                device.getDevAddr(),
                device.getAppEui(),
                device.getNwksKey(),
                device.getAppsKey(),
                device.getTags(),
                recentPositions,
                device.getFeatures(),
                recentLogs);
    }

}
