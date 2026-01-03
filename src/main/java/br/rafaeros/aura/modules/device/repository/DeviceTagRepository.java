package br.rafaeros.aura.modules.device.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.rafaeros.aura.modules.device.model.DeviceTag;

public interface DeviceTagRepository extends JpaRepository<DeviceTag, Long> {
    Optional<DeviceTag> findByName(String name);
}
