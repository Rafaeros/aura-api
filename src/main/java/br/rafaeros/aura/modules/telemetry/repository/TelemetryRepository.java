package br.rafaeros.aura.modules.telemetry.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaeros.aura.modules.telemetry.model.Telemetry;

@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
    List<Telemetry> findTop5ByDeviceIdOrderByCreatedAtDesc(Long deviceId);

    Page<Telemetry> findByDeviceIdOrderByCreatedAtDesc(Long deviceId, Pageable pageable);
}
