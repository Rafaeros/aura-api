package br.rafaeros.aura.modules.device.repository;

import br.rafaeros.aura.modules.device.model.DevicePosition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevicePositionRepository extends JpaRepository<DevicePosition, Long> {
    List<DevicePosition> findTop5ByDeviceIdOrderByCreatedAtDesc(Long deviceId);
}
