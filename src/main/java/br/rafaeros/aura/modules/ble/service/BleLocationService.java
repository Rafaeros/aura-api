package br.rafaeros.aura.modules.ble.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.modules.ble.controller.dto.BleDeviceDTO;
import br.rafaeros.aura.modules.ble.model.BleDevice;
import br.rafaeros.aura.modules.ble.repository.BleDeviceRepository;
import br.rafaeros.aura.modules.ble.repository.BleLocationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BleLocationService {

    private final BleLocationRepository bleLocationRepository;
    private final BleDeviceRepository bleDeviceRepository;

    public Page<BleDeviceDTO.Location> findLocationsByDevice(Long deviceId, Long companyId, Pageable pageable) {
        BleDevice device = bleDeviceRepository.findByIdAndCompanyId(deviceId, Objects.requireNonNull(companyId))
                .orElseThrow(
                        () -> new BusinessException("Dispositivo nao encontrado ou nao pertence a esta empresa."));

        return bleLocationRepository.findAllByBleDeviceId(device.getId(), pageable)
                .map(BleDeviceDTO.Location::fromEntity);
    }

}
