package br.rafaeros.aura.modules.company.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.rafaeros.aura.modules.company.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByCnpj(String cnpj);

    Optional<Company> findByCnpj(String cnpj);
}
