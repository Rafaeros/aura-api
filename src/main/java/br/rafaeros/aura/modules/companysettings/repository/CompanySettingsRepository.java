package br.rafaeros.aura.modules.companysettings.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.rafaeros.aura.modules.companysettings.model.CompanySettings;

public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {

    Optional<CompanySettings> findByCompanyId(Long companyId);

    boolean existsByCompanyId(Long companyId);
}
