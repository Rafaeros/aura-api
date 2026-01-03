package br.rafaeros.aura.modules.device.repository;

import br.rafaeros.aura.modules.device.model.DevicePosition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevicePositionRepository extends JpaRepository<DevicePosition, Long> {
    List<DevicePosition> findTop5ByDeviceIdOrderByCreatedAtDesc(Long deviceId);
}
