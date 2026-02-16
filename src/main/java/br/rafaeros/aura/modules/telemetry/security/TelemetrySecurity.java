package br.rafaeros.aura.modules.telemetry.security;

import org.springframework.stereotype.Component;

import br.rafaeros.aura.modules.device.repository.UserDeviceRepository;
import br.rafaeros.aura.modules.user.model.User;
import lombok.RequiredArgsConstructor;

@Component("telemetrySecurity")
@RequiredArgsConstructor
public class TelemetrySecurity {

    private final UserDeviceRepository userDeviceRepository;
    
    public boolean canViewTelemetry(Long deviceId, User user) {
        return userDeviceRepository.existsByUserIdAndDeviceId(user.getId(), deviceId);
    }

}
