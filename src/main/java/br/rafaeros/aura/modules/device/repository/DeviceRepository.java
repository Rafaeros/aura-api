package br.rafaeros.aura.modules.device.repository;

import br.rafaeros.aura.modules.device.model.Device;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    boolean existsByDevEui(String devEui);
    Optional<Device> findByDevEui(String devEui);
}