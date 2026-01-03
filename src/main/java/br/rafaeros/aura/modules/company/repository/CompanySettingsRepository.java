package br.rafaeros.aura.modules.company.repository;

import br.rafaeros.aura.modules.company.model.CompanySettings;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {

    Optional<CompanySettings> findByCompanyId(Long companyId);
}
