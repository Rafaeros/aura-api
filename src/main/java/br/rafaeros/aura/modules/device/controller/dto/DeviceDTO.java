package br.rafaeros.aura.modules.device.controller.dto;

import java.time.Instant;
import java.util.List;

import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.model.DeviceFeature;
import br.rafaeros.aura.modules.device.model.DevicePosition;
import br.rafaeros.aura.modules.device.model.DeviceTag;
import br.rafaeros.aura.modules.device.model.UserDevice;
import br.rafaeros.aura.modules.telemetry.controller.dto.TelemetryDTO;
import jakarta.validation.constraints.NotBlank;

public enum DeviceDTO {;

    public record RegisterRequest(
            @NotBlank(message = "O DEV EUI é obrigatório.") String devEui,
            String name) {
    }

    public record TagResponse(Long id, String name) {
        public static TagResponse fromEntity(DeviceTag tag) {
            return new TagResponse(tag.getId(), tag.getName());
        }
    }

    public record PositionResponse(Double latitude, Double longitude, Instant createdAt) {
        public static PositionResponse fromEntity(DevicePosition pos) {
            return new PositionResponse(pos.getLatitude(), pos.getLongitude(), pos.getCreatedAt());
        }
    }

    public record FeatureResponse(String name, String value) {
        public static FeatureResponse fromEntity(DeviceFeature feature) {
            return new FeatureResponse(feature.getName(), feature.getValue());
        }
    }

    public record Response(
            Long id,
            String name,
            String devEui,
            List<TagResponse> tags) {
        
        public static Response fromUserDevice(UserDevice userDevice) {
            Device d = userDevice.getDevice();
            List<TagResponse> safeTags = (d.getTags() != null) 
                ? d.getTags().stream().map(TagResponse::fromEntity).toList() 
                : List.of();

            return new Response(
                    d.getId(),
                    userDevice.getName() != null ? userDevice.getName() : "Dispositivo " + d.getDevEui(),
                    d.getDevEui(),
                    safeTags);
        }

        public static Response fromDevice(Device device) {
            List<TagResponse> safeTags = (device.getTags() != null) 
                ? device.getTags().stream().map(TagResponse::fromEntity).toList() 
                : List.of();

            return new Response(
                    device.getId(),
                    "Dispositivo " + device.getDevEui(),
                    device.getDevEui(),
                    safeTags);
        }
    }

    public record DetailsResponse(
            Long id,
            String name,
            String devEui,
            String devAddr,
            List<TagResponse> tags,
            List<PositionResponse> recentPositions,
            List<FeatureResponse> features,
            List<TelemetryDTO.Response> recentLogs) {
        
        public static DetailsResponse fromEntity(String customName, Device device,
                List<DevicePosition> positions,
                List<TelemetryDTO.Response> logs) {

            List<TagResponse> safeTags = (device.getTags() != null)
                    ? device.getTags().stream().map(TagResponse::fromEntity).toList()
                    : List.of();
                    
            List<FeatureResponse> safeFeatures = (device.getFeatures() != null)
                    ? device.getFeatures().stream().map(FeatureResponse::fromEntity).toList()
                    : List.of();

            List<PositionResponse> safePositions = (positions != null)
                    ? positions.stream().map(PositionResponse::fromEntity).toList()
                    : List.of();

            List<TelemetryDTO.Response> safeLogs = (logs != null) ? logs : List.of();

            return new DetailsResponse(
                    device.getId(),
                    customName,
                    device.getDevEui(),
                    device.getDevAddr(),
                    safeTags,
                    safePositions,
                    safeFeatures,
                    safeLogs);
        }
    }
}