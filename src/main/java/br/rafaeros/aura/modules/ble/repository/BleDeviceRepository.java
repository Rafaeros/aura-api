package br.rafaeros.aura.modules.ble.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaeros.aura.modules.ble.model.BleDevice;

@Repository
public interface BleDeviceRepository extends JpaRepository<BleDevice, Long> {

    boolean existsByHashedPublicKeyAndCompanyId(String hashedPublicKey, Long companyId);

    Optional<BleDevice> findByIdAndCompanyId(Long id, Long companyId);

    Page<BleDevice> findAllByCompanyId(Long companyId, Pageable pageable);
}