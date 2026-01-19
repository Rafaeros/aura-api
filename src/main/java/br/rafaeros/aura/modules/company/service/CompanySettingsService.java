package br.rafaeros.aura.modules.company.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsRequestDTO;
import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsResponseDTO;
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
    public CompanySettingsResponseDTO findMyCompanySettings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getCompany() == null) {
            throw new BusinessException("User is not associated with a company.");
        }

        return findByCompanyId(user.getCompany().getId());
    }

    @Transactional
    public CompanySettingsResponseDTO updateMyCompanySettings(String email, CompanySettingsRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getCompany() == null) {
            throw new BusinessException("User is not associated with a company.");
        }

        return updateByCompanyId(user.getCompany().getId(), dto, email);
    }

    @Transactional(readOnly = true)
    public CompanySettingsResponseDTO findByCompanyId(Long companyId) {
        if (companyId == null)
            throw new BusinessException("Company ID is required.");

        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with ID: " + companyId);
        }

        CompanySettings settings = settingsRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Settings not found for company ID: " + companyId));

        return CompanySettingsResponseDTO.fromEntity(settings);
    }

    @Transactional
    public CompanySettingsResponseDTO updateByCompanyId(Long companyId, CompanySettingsRequestDTO dto,
            String email) {
        if (companyId == null)
            throw new BusinessException("Company ID is required");

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        validateAccess(companyId, email);

        CompanySettings settings = settingsRepository.findByCompanyId(companyId)
                .orElse(new CompanySettings());

        if (settings.getId() == null) {
            settings.setCompany(company);
        }

        settings.setCompany(company);
        settings.setEverynetAccessToken(dto.everynetAccessToken());
        settings.setMqttHost(dto.mqttHost());
        settings.setMqttPort(dto.mqttPort());
        settings.setMqttUsername(dto.mqttUsername());
        settings.setSubscribeTopic(dto.subscribeTopic());
        settings.setPublishTopic(dto.publishTopic());

        if (dto.mqttPassword() != null && !dto.mqttPassword().isEmpty()) {
            settings.setMqttPassword(dto.mqttPassword());
        }

        CompanySettings savedSettings = settingsRepository.save(settings);
        CompanySettingsResponseDTO responseDto = CompanySettingsResponseDTO.fromEntity(savedSettings);
        return responseDto;
    }

    private void validateAccess(Long targetCompanyId, String email) {
        if (targetCompanyId == null) {
            throw new BusinessException("Target Company ID cannot be null.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            return;
        }

        if (user.getCompany() == null || !user.getCompany().getId().equals(targetCompanyId)) {
            throw new AccessDeniedException("Access denied: You do not belong to this company.");
        }
    }
}