package br.rafaeros.aura.modules.telemetry.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.model.DevicePosition;
import br.rafaeros.aura.modules.device.repository.DeviceRepository;
import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryRequestDTO;
import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryResponseDTO;
import br.rafaeros.aura.modules.telemetry.model.DeviceTelemetry;
import br.rafaeros.aura.modules.telemetry.repository.DeviceTelemetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTelemetryService {

    private final DeviceTelemetryRepository deviceTelemetryRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public DeviceTelemetryResponseDTO ingest(DeviceTelemetryRequestDTO request) {
        Device device = deviceRepository.findByDevEui(request.devEui())
                .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + request.devEui()));

        if (device.getUsersLink().isEmpty()) {
            log.warn("Device {} has no linked users. Telemetry ignored.", request.devEui());
            throw new BusinessException("Device not linked to any user.");
        }

        DeviceTelemetry telemetry = new DeviceTelemetry();
        telemetry.setDevice(device);
        telemetry.setSource(request.source());
        telemetry.setType(request.type());

        try {
            String payloadJson = objectMapper.writeValueAsString(request.payload());

            telemetry.setPayload(payloadJson);

            JsonNode payloadNode = objectMapper.valueToTree(request.payload());
            processLogLogic(device, request.type(), payloadNode);

        } catch (JsonProcessingException e) {
            throw new BusinessException("Error serializing telemetry: " + e.getMessage());
        }

        telemetry = deviceTelemetryRepository.save(telemetry);
        return DeviceTelemetryResponseDTO.fromEntity(telemetry);
    }

    @Transactional(readOnly = true)
    public List<DeviceTelemetryResponseDTO> findTop5ByDeviceId(Long deviceId) {
        if (!deviceRepository.existsById(Objects.requireNonNull(deviceId))) {
            throw new ResourceNotFoundException("Device not found with ID: " + deviceId);
        }

        return deviceTelemetryRepository.findTop5ByDeviceIdOrderByCreatedAtDesc(deviceId)
                .stream()
                .map(DeviceTelemetryResponseDTO::fromEntity)
                .toList();
    }

    private void processLogLogic(Device device, String type, JsonNode payload) {
        if (type == null)
            return;

        switch (type.toUpperCase()) {
            case "LOCATION":
                extractAndSaveLocation(device, payload);
                break;
            case "UPLINK":
                extractAndSaveUplink(device, payload);
                break;
            case "DOWNLINK":
                extractAndSaveDownlink(device, payload);
                break;
            default:
                break;
        }
    }

    private void extractAndSaveLocation(Device device, JsonNode paramsNode) {
        try {
            JsonNode params = paramsNode.path("params");
            JsonNode solutions = params.path("solutions");

            if (solutions.isArray() && !solutions.isEmpty()) {
                JsonNode firstSolution = solutions.get(0);

                double lat = firstSolution.path("lat").asDouble(0.0);
                double lng = firstSolution.path("lng").asDouble(0.0);

                if (lat != 0.0 && lng != 0.0) {
                    DevicePosition position = new DevicePosition();
                    position.setLatitude(lat);
                    position.setLongitude(lng);
                    device.addPosition(position);
                    log.info("GPS Location updated for device {}: {}, {}", device.getDevEui(), lat, lng);
                }
            } else {
                double lat = params.path("latitude").asDouble(params.path("lat").asDouble(0.0));
                double lng = params.path("longitude").asDouble(params.path("lng").asDouble(0.0));

                if (lat != 0.0 && lng != 0.0) {
                    DevicePosition position = new DevicePosition();
                    position.setLatitude(lat);
                    position.setLongitude(lng);
                    device.addPosition(position);
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract location for device {}", device.getDevEui(), e);
        }
    }

    private void extractAndSaveUplink(Device device, JsonNode payload) {
        try {
            JsonNode params = payload.path("params");
            JsonNode radio = params.path("radio");
            JsonNode hardware = radio.path("hardware");
            JsonNode gps = hardware.path("gps");
            if (gps.isMissingNode()) {
                gps = radio.path("gps");
            }

            double lat = gps.path("lat").asDouble(0.0);
            double lng = gps.path("lng").asDouble(0.0);
            if (lat != 0.0 && lng != 0.0) {
                DevicePosition position = new DevicePosition();
                position.setLatitude(lat);
                position.setLongitude(lng);
                device.addPosition(position);
                log.info("GPS Location updated (Uplink) for device {}: {}, {}", device.getDevEui(), lat, lng);
            }

        } catch (Exception e) {
            log.error("Failed to extract uplink location for device {}", device.getDevEui(), e);
        }
    }

    private void extractAndSaveDownlink(Device device, JsonNode payload) {
        try {
            JsonNode params = payload.path("params");
            JsonNode radio = params.path("radio");
            JsonNode hardware = radio.path("hardware");
            JsonNode gps = hardware.path("gps");
            if (gps.isMissingNode()) {
                gps = radio.path("gps");
            }

            double lat = gps.path("lat").asDouble(0.0);
            double lng = gps.path("lng").asDouble(0.0);
            if (lat != 0.0 && lng != 0.0) {
                DevicePosition position = new DevicePosition();
                position.setLatitude(lat);
                position.setLongitude(lng);
                device.addPosition(position);
                log.info("GPS Location updated (Uplink) for device {}: {}, {}", device.getDevEui(), lat, lng);
            }

        } catch (Exception e) {
            log.error("Failed to extract uplink location for device {}", device.getDevEui(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<DeviceTelemetryResponseDTO> listHistory(Long deviceId, Pageable pageable) {
        if (!deviceRepository.existsById(Objects.requireNonNull(deviceId))) {
            throw new ResourceNotFoundException("Device not found with ID: " + deviceId);
        }
        return deviceTelemetryRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId, pageable).map(DeviceTelemetryResponseDTO::fromEntity);
    }
}