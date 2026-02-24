package br.rafaeros.aura.modules.ble.controller.dto;

import java.time.Instant;
import java.util.List;

import br.rafaeros.aura.modules.ble.model.BleDevice;
import br.rafaeros.aura.modules.ble.model.BleLocation;
import jakarta.validation.constraints.NotBlank;

public class BleDeviceDTO {

        // Device
        public record CreateRequest(
                        @NotBlank(message = "O nome do dispositivo é obrigatório.") String name,

                        @NotBlank(message = "A chave pública do dispositivo é obrigatória.") String hashedPublicKey,

                        @NotBlank(message = "A chave privada do dispositivo é obrigatória.") String privateKeyBase64) {
        }

        public record Response(
                        String id,
                        String name,
                        String hashedPublicKey) {
                public static Response fromEntity(BleDevice device) {
                        return new Response(device.getId().toString(), device.getName(), device.getHashedPublicKey());
                }
        }

        public record DetailsResponse(
                        String id,
                        String name,
                        String hashedPublicKey,
                        List<Location> locations) {
                public static DetailsResponse from(BleDevice device, List<BleLocation> locationEntities) {
                        List<Location> mappedLocations = locationEntities.stream()
                                        .map(loc -> new Location(
                                                        loc.getId().toString(),
                                                        loc.getLatitude(),
                                                        loc.getLongitude(),
                                                        loc.getAccuracy(),
                                                        loc.getConfidence(),
                                                        loc.getBatteryStatus(),
                                                        loc.getTimestamp(),
                                                        loc.getPublishedAt()))
                                        .toList();

                        return new DetailsResponse(
                                        device.getId().toString(),
                                        device.getName(),
                                        device.getHashedPublicKey(),
                                        mappedLocations);
                }
        }

        // Location
        public record Location(
                        String id,
                        Double latitude,
                        Double longitude,
                        Integer accuracy,
                        Integer confidence,
                        String batteryStatus,
                        Instant timestamp,
                        Instant published) {
                public static Location fromEntity(BleLocation location) {
                        return new Location(
                                        location.getId().toString(),
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        location.getAccuracy(),
                                        location.getConfidence(),
                                        location.getBatteryStatus(),
                                        location.getTimestamp(),
                                        location.getPublishedAt());
                }
        }
}
