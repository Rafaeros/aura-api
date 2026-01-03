package br.rafaeros.aura.modules.device.service;

import lombok.extern.slf4j.Slf4j;
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

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Autowired
    private DeviceTagRepository tagRepository;

    @Autowired
    private DevicePositionRepository positionRepository;

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;
    private final EverynetClient everynetClient;

    public DeviceService(DeviceRepository deviceRepository,
            UserRepository userRepository,
            EverynetClient everynetClient,
            DevicePositionRepository positionRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.everynetClient = everynetClient;
        this.positionRepository = positionRepository;
    }

    public List<Device> listMyDevices(String username) {
        return deviceRepository.findAllByUsername(username);
    }

    @Transactional(readOnly = true)
    public Device findById(Long id, String username) {
        if (id == null) {
            throw new IllegalArgumentException("Device id must be provided");
        }

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Hibernate.initialize(device.getTags());
        Hibernate.initialize(device.getFeatures());

        entityManager.detach(device);

        List<DevicePosition> last5Positions = positionRepository.findTop5ByDeviceIdOrderByCreatedAtDesc(id);

        device.setPositions(last5Positions);

        return device;
    }

    @Transactional
    public Device registerDevice(DeviceCreateDTO dto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Device device = deviceRepository.findByDevEui(dto.devEui())
                .orElseGet(() -> {
                    log.info("Device not found locally. Fetching from Everynet: {}", dto.devEui());

                    EverynetDevice externalData = everynetClient.getDeviceByDevEui(dto.devEui());
                    Device newDevice = Objects.requireNonNull(Device.createFromEverynet(externalData, dto.name()),
                            "Failed to create device from Everynet data");
                    processTagsAndPosition(newDevice, externalData);
                    return deviceRepository.save(newDevice);
                });

        if (!currentUser.getDevices().contains(device)) {
            currentUser.getDevices().add(device);
            userRepository.save(currentUser);
            log.info("Device {} linked to user {}", dto.devEui(), username);
        } else {
            log.warn("Device {} is already linked to user {}", dto.devEui(), username);
        }

        return device;
    }

    @Transactional
    public void unlinkDevice(Long deviceId, String username) {

        if (deviceId == null) {
            throw new IllegalArgumentException("Device id must be provided");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (user.getDevices().contains(device)) {
            user.getDevices().remove(device);
            userRepository.save(user);
            log.info("Device {} unlinked from user {}", deviceId, username);
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