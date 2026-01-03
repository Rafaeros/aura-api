package br.rafaeros.aura.core.security;

import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    @Autowired private UserRepository userRepository;

    public boolean isOwnerOfCompany(Authentication authentication, Long targetCompanyId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (user.getRole() != Role.OWNER) {
            return false;
        }
        return user.getCompany().getId().equals(targetCompanyId);
    }

    public boolean isSelfOrAdmin(Authentication authentication, Long userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User loggedUser =
                userRepository
                        .findByUsername(authentication.getName())
                        .orElseThrow(() -> new RuntimeException("User not found"));

        if (loggedUser.getRole() == Role.ADMIN) {
            return true;
        }

        return loggedUser.getId().equals(userId);
    }

    public boolean belongsToCompany(Authentication authentication, Long targetCompanyId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getCompany().getId().equals(targetCompanyId);
    }

    public boolean canManageDevice(Authentication authentication, Long deviceId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        return user.getDevices().stream().anyMatch(device -> device.getId().equals(deviceId));
    }
}
