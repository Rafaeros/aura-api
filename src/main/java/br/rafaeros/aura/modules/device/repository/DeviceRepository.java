package br.rafaeros.aura.modules.device.repository;

import br.rafaeros.aura.modules.device.model.Device;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    @Query("SELECT d FROM Device d JOIN d.users u WHERE u.username = :username")
    List<Device> findAllByUsername(@Param("username") String username);

    boolean existsByDevEui(String devEui);

    Optional<Device> findByDevEui(String devEui);
}
