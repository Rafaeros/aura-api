package br.rafaeros.aura.modules.user.security;

import org.springframework.stereotype.Component;

import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;

@Component("userSecurity")
public class UserSecurity {

    public static boolean canViewProfile(Long userId, User user) {
        return user.getRole() == Role.ADMIN || user.getId().equals(userId);
    }

    public static boolean canChangePassword(Long userId, User user) {
        return user.getId().equals(userId);
    }
}
