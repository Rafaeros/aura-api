package br.rafaeros.aura.modules.ble.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.modules.ble.controller.dto.BleDeviceDTO;
import br.rafaeros.aura.modules.ble.model.BleDevice;
import br.rafaeros.aura.modules.ble.model.BleLocation;
import br.rafaeros.aura.modules.ble.repository.BleDeviceRepository;
import br.rafaeros.aura.modules.ble.repository.BleLocationRepository;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BleDeviceService {

    private final BleDeviceRepository bleDeviceRepository;
    private final BleLocationRepository bleLocationRepository;
    private final CompanyRepository companyRepository;

    public BleDeviceDTO.Response create(BleDeviceDTO.CreateRequest request, Long companyId) {
        boolean exists = bleDeviceRepository.existsByHashedPublicKeyAndCompanyId(request.hashedPublicKey(), companyId);
        if (exists) {
            throw new BusinessException("O dispositivo já existe!");
        }

        Company company = companyRepository.findById(Objects.requireNonNull(companyId))
                .orElseThrow(() -> new BusinessException("Empresa não encontrada."));

        BleDevice device = new BleDevice();
        device.setName(request.name());
        device.setHashedPublicKey(request.hashedPublicKey());
        device.setPrivateKeyBase64(request.privateKeyBase64());
        device.setCompany(company);

        bleDeviceRepository.save(device);
        return BleDeviceDTO.Response.fromEntity(device);
    }

    public Page<BleDeviceDTO.Response> findAllDevicesByCompanyId(Long companyId, Pageable pageable) {
        return bleDeviceRepository.findAllByCompanyId(Objects.requireNonNull(companyId), pageable)
                .map(BleDeviceDTO.Response::fromEntity);
    }

    public BleDeviceDTO.DetailsResponse findById(Long id, Long companyId) {
        BleDevice device = bleDeviceRepository.findByIdAndCompanyId(id, Objects.requireNonNull(companyId))
                .orElseThrow(() -> new BusinessException("Dispositivo não encontrado ou não pertence a esta empresa."));
        List<BleLocation> recentLocations = bleLocationRepository.findTop10ByBleDeviceIdOrderByTimestampDesc(id);
        return BleDeviceDTO.DetailsResponse.from(device, recentLocations);
    }

    public void delete(Long id, Long companyId) {
        BleDevice device = bleDeviceRepository.findByIdAndCompanyId(id, Objects.requireNonNull(companyId))
                .orElseThrow(() -> new BusinessException("Dispositivo não encontrado ou não pertence a esta empresa."));

        device.setActive(false);
        bleDeviceRepository.save(device);
    }
}
