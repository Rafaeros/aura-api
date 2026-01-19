package br.rafaeros.aura.modules.telemetry.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.repository.DeviceRepository;
import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryRequestDTO;
import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryResponseDTO;
import br.rafaeros.aura.modules.telemetry.model.DeviceTelemetry;
import br.rafaeros.aura.modules.telemetry.repository.DeviceTelemetryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceTelemetryService {
    private final DeviceTelemetryRepository deviceTelemetryRepository;
    private final DeviceRepository deviceRepository;

    private final ObjectMapper objectMapper;

    @Transactional
    public DeviceTelemetryResponseDTO create(DeviceTelemetryRequestDTO request) {
        Device device = deviceRepository.findByDevEui(request.devEui()).orElseThrow(
                () -> new ResourceNotFoundException("Device not found"));

        DeviceTelemetry telemetry = new DeviceTelemetry();
        telemetry.setDevice(device);
        telemetry.setType(request.type());

        try {
            String payloadJson = objectMapper.writeValueAsString(request.payload());
            String metadataJson = objectMapper.writeValueAsString(request.metadata());
            telemetry.setPayload(payloadJson);
            telemetry.setMetadata(metadataJson);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Error serializing telemetry: " + e.getMessage());
        }

        telemetry = deviceTelemetryRepository.save(telemetry);
        return DeviceTelemetryResponseDTO.fromEntity(telemetry);
    }

    @Transactional(readOnly = true)
    public Page<DeviceTelemetryResponseDTO> listHistory(Long deviceId, Pageable pageable) {
        if (!deviceRepository.existsById(Objects.requireNonNull(deviceId))) {
            throw new ResourceNotFoundException("Device not found with ID: " + deviceId);
        }
        return deviceTelemetryRepository.findByDeviceId(deviceId, pageable).map(DeviceTelemetryResponseDTO::fromEntity);
    }
}
