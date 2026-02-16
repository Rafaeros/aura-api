package br.rafaeros.aura.modules.device.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.IntegrationException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.companysettings.model.CompanySettings;
import br.rafaeros.aura.modules.device.client.EverynetClient;
import br.rafaeros.aura.modules.device.client.dto.EverynetDevice;
import br.rafaeros.aura.modules.device.controller.dto.DeviceDTO;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.model.DevicePosition;
import br.rafaeros.aura.modules.device.model.DeviceTag;
import br.rafaeros.aura.modules.device.model.UserDevice;
import br.rafaeros.aura.modules.device.repository.DevicePositionRepository;
import br.rafaeros.aura.modules.device.repository.DeviceRepository;
import br.rafaeros.aura.modules.device.repository.DeviceTagRepository;
import br.rafaeros.aura.modules.device.repository.UserDeviceRepository;
import br.rafaeros.aura.modules.telemetry.controller.dto.TelemetryDTO;
import br.rafaeros.aura.modules.telemetry.service.DeviceTelemetryService;
import br.rafaeros.aura.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final EverynetClient everynetClient;
    private final DeviceRepository deviceRepository;
    private final DevicePositionRepository positionRepository;
    private final DeviceTelemetryService deviceTelemetryService;
    private final UserDeviceRepository userDeviceRepository;
    private final DeviceTagRepository tagRepository;

    @Transactional(readOnly = true)
    public Page<DeviceDTO.Response> findAllUserDevices(User user, Pageable pageable) {
        Pageable safePageable = Objects.requireNonNull(pageable);
        return userDeviceRepository.findAllByUserId(user.getId(), safePageable)
                .map(DeviceDTO.Response::fromUserDevice);
    }

    @Transactional(readOnly = true)
    public DeviceDTO.DetailsResponse findById(Long id, User user) {
        Device device = deviceRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo não encontrado com ID: " + id));

        Optional<UserDevice> linkOpt = userDeviceRepository.findByUserIdAndDeviceId(user.getId(), id);

        String customName = linkOpt.map(UserDevice::getName).orElse(null);
        List<DevicePosition> recentPositions = positionRepository.findTop5ByDeviceIdOrderByCreatedAtDesc(id);
        List<TelemetryDTO.Response> recentLogs = deviceTelemetryService.findTop5ByDeviceId(id);

        DeviceDTO.DetailsResponse deviceResponse = DeviceDTO.DetailsResponse.fromEntity(customName, device,
                recentPositions,
                recentLogs);

        return deviceResponse;
    }

    public DeviceDTO.DetailsResponse findByDevEui(String devEui, String email) {
        Device device = deviceRepository.findByDevEui(devEui)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo não encontrado com DevEui: " + devEui));
        return DeviceDTO.DetailsResponse.fromEntity(null, device, null, null);
    }

    @Transactional
    public DeviceDTO.Response createDevice(DeviceDTO.RegisterRequest request, User user) {
        Device device = deviceRepository.findByDevEui(request.devEui())
                .orElseGet(() -> {
                    String apiToken = extractCompanyApiToken(user);
                    return fetchAndCreateFromEverynet(request, apiToken);
                });
        if (userDeviceRepository.existsByUserIdAndDeviceId(user.getId(), device.getId())) {
            throw new BusinessException("Dispositivo já vinculado.");
        }
        UserDevice link = new UserDevice(user, device, request.name());
        userDeviceRepository.save(link);
        log.info("Dispositivo {} vinculado...", request.devEui());
        return DeviceDTO.Response.fromUserDevice(link);
    }

    @Transactional
    public void unlinkDevice(Long deviceId, User user) {
        UserDevice link = userDeviceRepository.findByUserIdAndDeviceId(user.getId(), deviceId)
                .orElseThrow(() -> new BusinessException("Este dispositivo não está vinculado à sua conta."));

        userDeviceRepository.delete(Objects.requireNonNull(link));
        log.info("Dispositivo {} desvinculado do usuário {}", deviceId, user.getEmail());
    }

    private String extractCompanyApiToken(User user) {
        Company company = user.getCompany();
        if (company == null)
            throw new BusinessException("Usuário não está associado a uma empresa.");

        CompanySettings settings = company.getSettings();
        if (settings == null || settings.getEverynetAccessToken() == null
                || settings.getEverynetAccessToken().isEmpty()) {
            throw new BusinessException(
                    "Configurações da empresa (Integração Everynet) não foram configuradas corretamente.");
        }

        return settings.getEverynetAccessToken();
    }

    private Device fetchAndCreateFromEverynet(DeviceDTO.RegisterRequest request, String apiToken) {
        try {
            EverynetDevice externalData = everynetClient.getDeviceByDevEui(request.devEui(), apiToken);
            if (externalData == null)
                throw new ResourceNotFoundException("Dispositivo não encontrado no Everynet: " + request.devEui());

            Device newDevice = Device.createFromEverynet(externalData);
            if (newDevice == null)
                throw new IntegrationException("Falha ao mapear entidade Device.");

            processTagsAndPosition(newDevice, externalData);

            return deviceRepository.save(newDevice);

        } catch (Exception ex) {
            log.error("Erro de integração do Everynet", ex);
            if (ex instanceof ResourceNotFoundException || ex instanceof IntegrationException)
                throw ex;
            throw new IntegrationException("Erro do serviço externo: " + ex.getMessage());
        }
    }

    private void processTagsAndPosition(Device device, EverynetDevice externalData) {
        if (externalData.getTags() != null) {
            for (String tagName : externalData.getTags()) {
                DeviceTag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new DeviceTag(tagName)));
                device.addTag(tag);
            }
        }
        if (externalData.getGeolocation() != null) {
            DevicePosition pos = new DevicePosition();
            pos.setLatitude(externalData.getGeolocation().getLat());
            pos.setLongitude(externalData.getGeolocation().getLng());
            device.addPosition(pos);
        }
    }
}