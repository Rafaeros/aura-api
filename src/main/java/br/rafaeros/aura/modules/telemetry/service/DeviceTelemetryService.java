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
import br.rafaeros.aura.modules.telemetry.controller.dto.TelemetryDTO;
import br.rafaeros.aura.modules.telemetry.model.Telemetry;
import br.rafaeros.aura.modules.telemetry.repository.TelemetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTelemetryService {

    private final TelemetryRepository deviceTelemetryRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public TelemetryDTO.Response ingest(TelemetryDTO.CreateRequest request) {
        Device device = deviceRepository.findByDevEui(request.devEui())
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo não encontrado: " + request.devEui()));

        if (device.getUsersLink().isEmpty()) {
            log.warn("Dispositivo {} não possui usuários vinculados. Telemetria ignorada.", request.devEui());
            throw new BusinessException("Dispositivo não vinculado a nenhum usuário.");
        }

        Telemetry telemetry = new Telemetry();
        telemetry.setDevice(device);
        telemetry.setSource(request.source());
        telemetry.setType(request.type());

        try {
            String payloadJson = objectMapper.writeValueAsString(request.payload());

            telemetry.setPayload(payloadJson);

            JsonNode payloadNode = objectMapper.valueToTree(request.payload());
            processLogLogic(device, request.type(), payloadNode);

        } catch (JsonProcessingException e) {
            throw new BusinessException("Erro ao serializar telemetria: " + e.getMessage());
        }

        telemetry = deviceTelemetryRepository.save(telemetry);
        return TelemetryDTO.Response.fromEntity(telemetry);
    }

    @Transactional(readOnly = true)
    public List<TelemetryDTO.Response> findTop5ByDeviceId(Long deviceId) {
        if (!deviceRepository.existsById(Objects.requireNonNull(deviceId))) {
            throw new ResourceNotFoundException("Dispositivo não encontrado com ID: " + deviceId);
        }

        return deviceTelemetryRepository.findTop5ByDeviceIdOrderByCreatedAtDesc(deviceId)
                .stream()
                .map(TelemetryDTO.Response::fromEntity)
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

    @Transactional(readOnly = true)
    public Page<TelemetryDTO.Response> listHistory(Long deviceId, Pageable pageable) {
        if (!deviceRepository.existsById(Objects.requireNonNull(deviceId))) {
            throw new ResourceNotFoundException("Dispositivo não encontrado com ID: " + deviceId);
        }
        return deviceTelemetryRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId, pageable).map(TelemetryDTO.Response::fromEntity);
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
                    log.info("Localização GPS atualizada para o dispositivo {}: {}, {}", device.getDevEui(), lat, lng);
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
            log.error("Falha ao extrair localização do dispositivo {}", device.getDevEui(), e);
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
                log.info("Localização GPS atualizada (Uplink) para o dispositivo {}: {}, {}", device.getDevEui(), lat, lng);
            }

        } catch (Exception e) {
            log.error("Falha ao extrair localização de uplink do dispositivo {}", device.getDevEui(), e);
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
                log.info("Localização GPS atualizada (Uplink) para o dispositivo {}: {}, {}", device.getDevEui(), lat, lng);
            }

        } catch (Exception e) {
            log.error("Falha ao extrair localização de uplink do dispositivo {}", device.getDevEui(), e);
        }
    }


}