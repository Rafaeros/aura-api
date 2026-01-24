package br.rafaeros.aura.modules.device.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.IntegrationException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.model.CompanySettings;
import br.rafaeros.aura.modules.device.client.EverynetClient;
import br.rafaeros.aura.modules.device.client.dto.EverynetDevice;
import br.rafaeros.aura.modules.device.controller.dto.DeviceCreateRequestDTO;
import br.rafaeros.aura.modules.device.controller.dto.DeviceDetailsResponseDTO;
import br.rafaeros.aura.modules.device.controller.dto.DeviceListResponseDTO;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.model.DevicePosition;
import br.rafaeros.aura.modules.device.model.DeviceTag;
import br.rafaeros.aura.modules.device.model.UserDevice;
import br.rafaeros.aura.modules.device.repository.DevicePositionRepository;
import br.rafaeros.aura.modules.device.repository.DeviceRepository;
import br.rafaeros.aura.modules.device.repository.DeviceTagRepository;
import br.rafaeros.aura.modules.device.repository.UserDeviceRepository;
import br.rafaeros.aura.modules.telemetry.controller.dto.DeviceTelemetryResponseDTO;
import br.rafaeros.aura.modules.telemetry.service.DeviceTelemetryService;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeviceService {

    private final EverynetClient everynetClient;

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DevicePositionRepository positionRepository;
    private final DeviceTelemetryService deviceTelemetryService;

    @Autowired
    private UserDeviceRepository userDeviceRepository;
    @Autowired
    private DeviceTagRepository tagRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public DeviceService(
            EverynetClient everynetClient,
            DeviceRepository deviceRepository,
            UserRepository userRepository,
            DevicePositionRepository positionRepository,
            DeviceTelemetryService deviceTelemetryService) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.everynetClient = everynetClient;
        this.positionRepository = positionRepository;
        this.deviceTelemetryService = deviceTelemetryService;
    }

    @Transactional(readOnly = true)
    public Page<DeviceListResponseDTO> listDevicesSmart(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable safePageable = Objects.requireNonNull(pageable);

        if (user.getRole() == Role.ADMIN) {
            return deviceRepository.findAll(safePageable)
                    .map(DeviceListResponseDTO::fromDevice);

        } else {
            return userDeviceRepository.findAllByUserEmail(email, pageable)
                    .map(DeviceListResponseDTO::fromUserDevice);
        }
    }

    @Transactional(readOnly = true)
    public DeviceDetailsResponseDTO findById(Long id, String email) {
        if (id == null)
            throw new BusinessException("Device ID is required.");

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == Role.ADMIN;
        Optional<UserDevice> linkOpt = userDeviceRepository.findByUserEmailAndDeviceId(email, id);

        if (!isAdmin && linkOpt.isEmpty()) {
            throw new AccessDeniedException("Access denied: You do not have permission to view this device.");
        }
        String customName = linkOpt.map(UserDevice::getCustomName).orElse(null);
        List<DevicePosition> recentPositions = positionRepository.findTop5ByDeviceIdOrderByCreatedAtDesc(id);
        List<DeviceTelemetryResponseDTO> recentLogs = deviceTelemetryService.findTop5ByDeviceId(id);

        DeviceDetailsResponseDTO deviceResponse = DeviceDetailsResponseDTO.fromEntity(customName, device,
                recentPositions,
                recentLogs);

        return deviceResponse;
    }

    public DeviceDetailsResponseDTO findByDevEui(String devEui, String email) {
        Device device = deviceRepository.findByDevEui(devEui)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with DevEui: " + devEui));
        return DeviceDetailsResponseDTO.fromEntity(null, device, null, null);
    }

    public boolean existsByDevEui(String devEui) {
        return deviceRepository.existsByDevEui(devEui);
    }

    @Transactional
    public Device createDevice(DeviceCreateRequestDTO dto, String email) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Device> existingDevice = deviceRepository.findByDevEui(dto.devEui());
        Device device;

        if (existingDevice.isPresent()) {
            device = existingDevice.get();
        } else {
            String apiToken = extractCompanyApiToken(currentUser);
            device = fetchAndCreateFromEverynet(dto, apiToken);
        }

        if (userDeviceRepository.existsByUserEmailAndDeviceId(email, device.getId())) {
            throw new BusinessException("Device " + dto.devEui() + " is already linked to your account.");
        }

        device.addUser(currentUser, dto.name());
        deviceRepository.save(device);
        log.info("Device {} linked to user {}", dto.devEui(), email);

        return device;
    }

    @Transactional
    public void unlinkDevice(Long deviceId, String email) {
        if (deviceId == null)
            throw new BusinessException("Device ID is required.");

        UserDevice link = userDeviceRepository.findByUserEmailAndDeviceId(email, deviceId)
                .orElseThrow(() -> new BusinessException("This device is not linked to your account."));

        userDeviceRepository.delete(Objects.requireNonNull(link));
        log.info("Device {} unlinked from user {}", deviceId, email);
    }

    private String extractCompanyApiToken(User user) {
        Company company = user.getCompany();
        if (company == null)
            throw new BusinessException("User is not associated with a company.");

        CompanySettings settings = company.getSettings();
        if (settings == null || settings.getEverynetAccessToken() == null
                || settings.getEverynetAccessToken().isEmpty()) {
            throw new BusinessException("Company settings (Everynet Integration) are not configured properly.");
        }

        return settings.getEverynetAccessToken();
    }

    private Device fetchAndCreateFromEverynet(DeviceCreateRequestDTO dto, String apiToken) {
        try {
            EverynetDevice externalData = everynetClient.getDeviceByDevEui(dto.devEui(), apiToken);
            if (externalData == null)
                throw new ResourceNotFoundException("Device not found in Everynet: " + dto.devEui());

            Device newDevice = Device.createFromEverynet(externalData);
            if (newDevice == null)
                throw new IntegrationException("Failed to map Device entity.");

            processTagsAndPosition(newDevice, externalData);

            return deviceRepository.save(newDevice);

        } catch (Exception ex) {
            log.error("Everynet integration error", ex);
            if (ex instanceof ResourceNotFoundException || ex instanceof IntegrationException)
                throw ex;
            throw new IntegrationException("External service error: " + ex.getMessage());
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