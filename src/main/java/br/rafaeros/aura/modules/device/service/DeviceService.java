package br.rafaeros.aura.modules.device.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final EverynetClient everynetClient;
    private final DevicePositionRepository positionRepository;

    @Autowired
    private DeviceTagRepository tagRepository;
    @PersistenceContext
    private EntityManager entityManager;

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
    public List<Device> listDevicesSmart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            log.info("Admin {} is listing ALL devices in the system.", email);
            return deviceRepository.findAll();
        } else {
            log.info("User/Owner {} is listing their OWN devices.", email);
            return user.getDevices();
        }
    }

    @Transactional(readOnly = true)
    public Device findById(Long id, String email) {
        if (id == null)
            throw new BusinessException("Device ID is required.");

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOwnerOfDevice = user.getDevices().contains(device);

        if (!isAdmin && !isOwnerOfDevice) {
            throw new AccessDeniedException("Access denied: You do not have permission to view this device.");
        }

        Hibernate.initialize(device.getTags());
        Hibernate.initialize(device.getFeatures());
        entityManager.detach(device);

        List<DevicePosition> last5Positions = positionRepository.findTop5ByDeviceIdOrderByCreatedAtDesc(id);
        device.setPositions(last5Positions);

        return device;
    }

    @Transactional
    public Device createDevice(DeviceCreateDTO dto, String email) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Device device = deviceRepository.findByDevEui(dto.devEui())
                .orElseGet(() -> fetchAndCreateFromEverynet(dto));

        if (currentUser.getDevices().contains(device)) {
            throw new BusinessException("Device " + dto.devEui() + " is already linked to your account.");
        }

        currentUser.getDevices().add(device);
        userRepository.save(currentUser);
        log.info("Device {} linked to user {}", dto.devEui(), email);

        return device;
    }

    @Transactional
    public void unlinkDevice(Long deviceId, String email) {
        if (deviceId == null)
            throw new BusinessException("Device ID is required.");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + deviceId));

        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOwnerOfDevice = user.getDevices().contains(device);

        if (!isOwnerOfDevice && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to unlink this device.");
        }

        if (user.getDevices().contains(device)) {
            user.getDevices().remove(device);
            userRepository.save(user);
            log.info("Device {} unlinked from user {}", deviceId, email);
        } else {
            throw new BusinessException("This device is not linked to your account.");
        }
    }

    private Device fetchAndCreateFromEverynet(DeviceCreateDTO dto) {
        try {
            EverynetDevice externalData = everynetClient.getDeviceByDevEui(dto.devEui());
            if (externalData == null)
                throw new ResourceNotFoundException("Device not found in Everynet: " + dto.devEui());

            Device newDevice = Device.createFromEverynet(externalData, dto.name());
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
            pos.setCreatedAt(OffsetDateTime.now());
            device.addPosition(pos);
        }
    }
}