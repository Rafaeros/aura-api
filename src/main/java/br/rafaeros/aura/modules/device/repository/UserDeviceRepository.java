package br.rafaeros.aura.modules.device.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaeros.aura.modules.device.model.UserDevice;
import br.rafaeros.aura.modules.device.model.UserDeviceId;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, UserDeviceId> {
    Page<UserDevice> findAllByUserEmail(String email, Pageable pageable);

    @EntityGraph(attributePaths = {"device", "device.tags"})
    Page<UserDevice> findAllByUserId(Long userId, Pageable pageable);

    boolean existsByUserEmailAndDeviceId(String email, Long deviceId);
    boolean existsByUserIdAndDeviceId(Long userId, Long deviceId);

    Optional<UserDevice> findByUserEmailAndDeviceId(String email, Long deviceId);
    Optional<UserDevice> findByUserIdAndDeviceId(Long userId, Long deviceId);
}