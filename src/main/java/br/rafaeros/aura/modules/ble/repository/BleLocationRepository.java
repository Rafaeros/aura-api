package br.rafaeros.aura.modules.ble.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaeros.aura.modules.ble.model.BleLocation;

@Repository
public interface BleLocationRepository extends JpaRepository<BleLocation, Long> {
    List<BleLocation> findTop10ByBleDeviceIdOrderByTimestampDesc(Long bleDeviceId);

    Page<BleLocation> findAllByBleDeviceId(Long bleDeviceId, Pageable pageable);
}