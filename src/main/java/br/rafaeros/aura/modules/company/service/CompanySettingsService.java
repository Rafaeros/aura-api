package br.rafaeros.aura.modules.company.service;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsDTO;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.model.CompanySettings;
import br.rafaeros.aura.modules.company.repository.CompanySettingsRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CompanySettingsService {

    private final CompanySettingsRepository repository;
    private final UserRepository userRepository;

    public CompanySettingsService(CompanySettingsRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public CompanySettings getByCompanyId(Long companyId) {
        if (companyId == null) {
            throw new BusinessException("Company ID is required.");
        }
        return repository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Settings not found for company ID: " + companyId));
    }

    @Transactional
    public CompanySettings saveOrUpdate(String username, CompanySettingsDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Company userCompany = user.getCompany();
        if (userCompany == null) {
            throw new BusinessException("User is not linked to any company.");
        }

        CompanySettings settings = repository.findByCompanyId(userCompany.getId())
                .orElse(new CompanySettings());

        if (settings.getId() == null) {
            settings.setCompany(userCompany);
        }

        settings.setEverynetAccessToken(dto.everynetAccessToken());
        settings.setMqttHost(dto.mqttHost());
        settings.setMqttPort(dto.mqttPort());
        settings.setMqttUsername(dto.mqttUsername());
        settings.setMqttPassword(dto.mqttPassword());

        return repository.save(Objects.requireNonNull(settings));
    }
}