package br.rafaeros.aura.modules.device.service;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.IntegrationException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.device.client.EverynetClient;
import br.rafaeros.aura.modules.device.client.dto.EverynetDevice;
import br.rafaeros.aura.modules.device.controller.dto.DeviceCreateDTO;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.model.DevicePosition;
import br.rafaeros.aura.modules.device.model.DeviceTag;
import br.rafaeros.aura.modules.device.repository.DevicePositionRepository;
import br.rafaeros.aura.modules.device.repository.DeviceRepository;
import br.rafaeros.aura.modules.device.repository.DeviceTagRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Autowired private DeviceTagRepository tagRepository;

    @Autowired private DevicePositionRepository positionRepository;

    private final UserRepository userRepository;

    @PersistenceContext private EntityManager entityManager;
    private final EverynetClient everynetClient;

    public DeviceService(
            DeviceRepository deviceRepository,
            UserRepository userRepository,
            EverynetClient everynetClient,
            DevicePositionRepository positionRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.everynetClient = everynetClient;
        this.positionRepository = positionRepository;
    }

    @Transactional(readOnly = true)
    public List<Device> listMyDevices(String username) {
        return deviceRepository.findAllByUsername(username);
    }

    @Transactional(readOnly = true)
    public Device findById(Long id, String username) {
        if (id == null) {
            throw new BusinessException("Device ID is required.");
        }
        Device device =
                deviceRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Device not found with ID: " + id));

        Hibernate.initialize(device.getTags());
        Hibernate.initialize(device.getFeatures());
        entityManager.detach(device);
        List<DevicePosition> last5Positions =
                positionRepository.findTop5ByDeviceIdOrderByCreatedAtDesc(id);
        device.setPositions(last5Positions);

        return device;
    }

    @Transactional
    public Device createDevice(DeviceCreateDTO dto, String username) {
        User currentUser =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("User not found: " + username));

        Device device =
                deviceRepository
                        .findByDevEui(dto.devEui())
                        .orElseGet(() -> fetchAndCreateFromEverynet(dto));

        if (currentUser.getDevices().contains(device)) {
            throw new BusinessException(
                    "Device " + dto.devEui() + " is already linked to your account.");
        }

        currentUser.getDevices().add(device);
        userRepository.save(currentUser);
        log.info("Device {} linked to user {}", dto.devEui(), username);

        return device;
    }

    @Transactional
    public void unlinkDevice(Long deviceId, String username) {
        if (deviceId == null) {
            throw new BusinessException("Device ID is required.");
        }

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("User not found: " + username));

        Device device =
                deviceRepository
                        .findById(deviceId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Device not found with ID: " + deviceId));

        if (user.getDevices().contains(device)) {
            user.getDevices().remove(device);
            userRepository.save(user);
            log.info("Device {} unlinked from user {}", deviceId, username);
        } else {
            throw new BusinessException("This device is not linked to your account.");
        }
    }

    private Device fetchAndCreateFromEverynet(DeviceCreateDTO dto) {
        log.info("Device not found locally. Fetching from Everynet: {}", dto.devEui());

        try {
            EverynetDevice externalData = everynetClient.getDeviceByDevEui(dto.devEui());

            if (externalData == null) {
                throw new ResourceNotFoundException(
                        "Device not found in Everynet network: " + dto.devEui());
            }

            Device newDevice = Device.createFromEverynet(externalData, dto.name());

            if (newDevice == null) {
                throw new IntegrationException("Failed to map Everynet data to Device entity.");
            }

            processTagsAndPosition(newDevice, externalData);
            return deviceRepository.save(newDevice);

        } catch (Exception ex) {
            log.error("Error integrating with Everynet", ex);
            if (ex instanceof ResourceNotFoundException || ex instanceof IntegrationException) {
                throw ex;
            }
            throw new IntegrationException(
                    "External service unavailable or returned invalid data: " + ex.getMessage());
        }
    }

    private void processTagsAndPosition(Device device, EverynetDevice externalData) {
        if (externalData.getTags() != null) {
            for (String tagName : externalData.getTags()) {
                DeviceTag tag =
                        tagRepository
                                .findByName(tagName)
                                .orElseGet(() -> tagRepository.save(new DeviceTag(tagName)));
                device.addTag(tag);
            }
        }
        if (externalData.getGeolocation() != null) {
            DevicePosition pos = new DevicePosition();
            pos.setLatitude(externalData.getGeolocation().getLat());
            pos.setLongitude(externalData.getGeolocation().getLng());
            pos.setCreatedAt(OffsetDateTime.now());
            device.addPosition(pos);
        }
    }
}
