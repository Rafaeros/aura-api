package br.rafaeros.aura.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;

@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private UserRepository userRepository;

    public boolean isOwnerOfCompany(Authentication authentication, Long targetCompanyId) {
        User user = getUser(authentication);

        if (user.getRole() == Role.ADMIN)
            return true;
        if (user.getRole() != Role.OWNER)
            return false;

        return user.getCompany().getId().equals(targetCompanyId);
    }

    public boolean isSelfOrAdmin(Authentication authentication, Long userId) {
        User user = getUser(authentication);
        if (user.getRole() == Role.ADMIN)
            return true;
        return user.getId().equals(userId);
    }

    public boolean belongsToCompany(Authentication authentication, Long targetCompanyId) {
        User user = getUser(authentication);
        return user.getCompany().getId().equals(targetCompanyId);
    }

    public boolean canManageDevice(Authentication authentication, Long deviceId) {
        User user = getUser(authentication);
        if (user.getRole() == Role.ADMIN)
            return true;

        return user.getDevices().stream()
                .anyMatch(device -> device.getId().equals(deviceId));
    }

    private User getUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}