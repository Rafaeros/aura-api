package br.rafaeros.aura.modules.device.repository;

import br.rafaeros.aura.modules.device.model.DeviceTag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTagRepository extends JpaRepository<DeviceTag, Long> {
    Optional<DeviceTag> findByName(String name);
}
