package br.rafaeros.aura.modules.companysettings.security;

import org.springframework.stereotype.Component;

import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;

@Component("companySettingsSecurity")
public class CompanySettingsSecurity {
    public boolean canViewGeneralSettings(Long companyId, User user) {
        if (user.getRole() == Role.ADMIN)
            return true;

        return belongsToCompany(companyId, user);
    }

    public boolean canViewSensitiveData(Long companyId, User user) {
        return belongsToCompany(companyId, user) && user.getRole() == Role.OWNER;
    }

    public boolean canEditSettings(Long companyId, User user) {
        return canViewSensitiveData(companyId, user);
    }

    public boolean canConnectMQTT(Long companyId, User user) {
        return belongsToCompany(companyId, user);
    }

    private boolean belongsToCompany(Long companyId, User user) {
        return user.getCompany().getId().equals(companyId);
    }
}
