package br.rafaeros.aura.modules.company.security;

import org.springframework.stereotype.Component;

import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;

@Component("companySecurity")
public class CompanySecurity {

    public boolean canAccessCompany(Long companyIdRequest, User userPrincipal) {
        if (userPrincipal.getRole() == Role.ADMIN) {
            return true;
        }
        return userPrincipal.getCompany() != null
                && userPrincipal.getCompany().getId().equals(companyIdRequest);
    }
}