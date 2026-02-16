package br.rafaeros.aura.modules.device.security;

import org.springframework.stereotype.Component;

import br.rafaeros.aura.modules.device.repository.UserDeviceRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import lombok.RequiredArgsConstructor;

@Component("deviceSecurity")
@RequiredArgsConstructor
public class DeviceSecurity {

    private final UserDeviceRepository userDeviceRepository;

    public boolean canViewDevice(Long deviceId, User user) {
        if (user.getRole() == Role.ADMIN)
            return true;

        return userDeviceRepository.existsByUserIdAndDeviceId(user.getId(), deviceId);
    }

    public boolean canUnlinkDevice(Long deviceId, User user) {
        return canViewDevice(deviceId, user);
    }
}
