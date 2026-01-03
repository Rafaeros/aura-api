package br.rafaeros.aura.modules.company.service;

import org.springframework.stereotype.Service;

import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsDTO;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.model.CompanySettings;
import br.rafaeros.aura.modules.company.repository.CompanySettingsRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class CompanySettingsService {

    private final CompanySettingsRepository repository;
    private final UserRepository userRepository;

    public CompanySettingsService(CompanySettingsRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public CompanySettings getByCompanyId(Long companyId) {
        return repository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Settings not found for company ID: " + companyId));
    }

    @Transactional
    public CompanySettings saveOrUpdate(String username, CompanySettingsDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Company userCompany = user.getCompany();
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
        return repository.save(settings);
    }
}