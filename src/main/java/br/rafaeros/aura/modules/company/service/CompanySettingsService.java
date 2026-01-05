package br.rafaeros.aura.modules.company.service;

import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsDTO;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.model.CompanySettings;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import br.rafaeros.aura.modules.company.repository.CompanySettingsRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;

@Service
public class CompanySettingsService {

    private final CompanySettingsRepository settingsRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanySettingsService(
            CompanySettingsRepository settingsRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.settingsRepository = settingsRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public CompanySettings findByCompanyId(Long companyId, String userEmail) {
        if (companyId == null)
            throw new BusinessException("Company ID is required.");

        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with ID: " + companyId);
        }

        validateAccess(companyId, userEmail);

        return settingsRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Settings not found for company ID: " + companyId));
    }

    @Transactional
    public CompanySettings updateByCompanyId(Long companyId, CompanySettingsDTO dto, String userEmail) {
        if (companyId == null)
            throw new BusinessException("Company ID is required.");

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        validateAccess(companyId, userEmail);

        CompanySettings settings = settingsRepository.findByCompanyId(companyId)
                .orElse(new CompanySettings());

        if (settings.getId() == null) {
            settings.setCompany(company);
        }

        settings.setEverynetAccessToken(dto.everynetAccessToken());
        settings.setMqttHost(dto.mqttHost());
        settings.setMqttPort(dto.mqttPort());
        settings.setMqttUsername(dto.mqttUsername());
        settings.setMqttPassword(dto.mqttPassword());

        return settingsRepository.save(Objects.requireNonNull(settings));
    }

    private void validateAccess(Long targetCompanyId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            return;
        }

        if (user.getCompany() == null || !user.getCompany().getId().equals(targetCompanyId)) {
            throw new AccessDeniedException("Access denied: You do not belong to this company.");
        }

    }
}