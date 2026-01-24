package br.rafaeros.aura.modules.telemetry.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.rafaeros.aura.modules.telemetry.model.DeviceTelemetry;

public interface DeviceTelemetryRepository extends JpaRepository<DeviceTelemetry, Long> {
    List<DeviceTelemetry> findTop5ByDeviceIdOrderByCreatedAtDesc(Long deviceId);

    Page<DeviceTelemetry> findByDeviceIdOrderByCreatedAtDesc(Long deviceId, Pageable pageable);
}
