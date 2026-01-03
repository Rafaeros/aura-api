package br.rafaeros.aura.modules.company.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.rafaeros.aura.modules.company.model.CompanySettings;

public interface CompanySettingsRepository
        extends JpaRepository<CompanySettings, Long> {

    Optional<CompanySettings> findByCompanyId(Long companyId);
}