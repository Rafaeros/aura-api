package br.rafaeros.aura.modules.companysettings.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.service.CompanyService;
import br.rafaeros.aura.modules.companysettings.controller.dto.CompanySettingsDTO;
import br.rafaeros.aura.modules.companysettings.model.CompanySettings;
import br.rafaeros.aura.modules.companysettings.repository.CompanySettingsRepository;
import br.rafaeros.aura.modules.user.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanySettingsService {

    private final CompanySettingsRepository companySettingsRepository;
    private final CompanyService companyService;

    @Transactional
    public CompanySettingsDTO.Response create(Long companyId, CompanySettingsDTO.CreateRequest request, User user) {

        Company company = companyService.findByIdInternal(companyId);

        CompanySettings settings = new CompanySettings();
        settings.setCompany(company);
        settings.setEverynetAccessToken(request.everynetAccessToken());
        settings.setMqttHost(request.mqttHost());
        settings.setMqttPort(request.mqttPort());
        settings.setMqttUsername(request.mqttUsername());
        settings.setMqttPassword(request.mqttPassword());
        settings.setSubscribeTopic(request.subscribeTopic());
        settings.setPublishTopic(request.publishTopic());
        settings = companySettingsRepository.save(settings);
        return CompanySettingsDTO.Response.fromEntity(settings, user.getRole());
    }

    @Transactional(readOnly = true)
    public CompanySettingsDTO.Response findByCompanyId(Long companyId, User user) {
        CompanySettings settings = findCompanySettingsByCompanyId(companyId);
        return CompanySettingsDTO.Response.fromEntity(settings, user.getRole());
    }

    @Transactional(readOnly = true)
    public CompanySettingsDTO.MqttConnectionCredentials findMqttSettingsByCompanyId(Long companyId) {
        CompanySettings settings = findCompanySettingsByCompanyId(companyId);
        return CompanySettingsDTO.MqttConnectionCredentials.fromEntity(settings);
    }

    @Transactional
    public CompanySettingsDTO.Response update(Long companyId, CompanySettingsDTO.UpdateRequest request, User user) {
        CompanySettings settings = findCompanySettingsByCompanyId(companyId);

        if (settings == null) {
            throw new ResourceNotFoundException("Configurações não encontradas.");
        }

        if (settings != null) {
            if (request.everynetAccessToken() != null) {
                settings.setEverynetAccessToken(request.everynetAccessToken());
            }
            if (request.mqttHost() != null) {
                settings.setMqttHost(request.mqttHost());
            }
            if (request.mqttPort() != null) {
                settings.setMqttPort(request.mqttPort());
            }
            if (request.mqttUsername() != null) {
                settings.setMqttUsername(request.mqttUsername());
            }
            if (request.mqttPassword() != null) {
                settings.setMqttPassword(request.mqttPassword());
            }
            if (request.subscribeTopic() != null) {
                settings.setSubscribeTopic(request.subscribeTopic());
            }
            if (request.publishTopic() != null) {
                settings.setPublishTopic(request.publishTopic());
            }
        }
        CompanySettings updated = companySettingsRepository.save(settings);

        return CompanySettingsDTO.Response.fromEntity(updated, user.getRole());
    }

    @Transactional(readOnly = true)
    private CompanySettings findCompanySettingsByCompanyId(Long companyId) {
        return companySettingsRepository.findByCompanyId(companyId).orElseThrow(
                () -> new ResourceNotFoundException("Configuração de empresa não encontrada para o ID: " + companyId));
    }

}