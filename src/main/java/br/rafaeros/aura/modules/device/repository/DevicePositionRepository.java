package br.rafaeros.aura.modules.device.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.rafaeros.aura.modules.device.model.DevicePosition;

public interface DevicePositionRepository extends JpaRepository<DevicePosition, Long> {
    List<DevicePosition> findTop5ByDeviceIdOrderByCreatedAtDesc(Long deviceId);
}
