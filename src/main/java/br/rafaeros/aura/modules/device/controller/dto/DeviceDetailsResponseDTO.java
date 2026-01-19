package br.rafaeros.aura.modules.device.controller.dto;

import java.util.List;

import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.model.DeviceFeature;
import br.rafaeros.aura.modules.device.model.DevicePosition;
import br.rafaeros.aura.modules.device.model.DeviceTag;
import br.rafaeros.aura.modules.telemetry.model.DeviceTelemetry;

public record DeviceDetailsResponseDTO (
    Long id,
    String name,
    String devEui,
    String devAddr,
    String appEui,
    String nwksKey,
    String appsKey,
    List<DeviceTag> tags,
    List<DevicePosition> recentPositions,
    List<DeviceFeature> features,
    List<DeviceTelemetry> recentLogs
) {
    public static DeviceDetailsResponseDTO fromEntity(String customName,Device device, List<DevicePosition> recentPositions, List<DeviceTelemetry> recentLogs) {
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
            recentLogs
        );
    }

}
