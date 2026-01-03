package br.rafaeros.aura.modules.company.service;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.controller.dto.CompanyDTO;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public Company create(CompanyDTO dto) {
        if (companyRepository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("Company with CNPJ " + dto.cnpj() + " already exists.");
        }

        Company company = new Company(dto.name(), dto.cnpj(), dto.cep(), dto.addressNumber());
        return companyRepository.save(company);
    }

    @Transactional(readOnly = true)
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Company findById(Long id) {
        if (id == null) {
            throw new BusinessException("Company ID is required.");
        }
        return companyRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Company not found with ID: " + id));
    }

    @Transactional
    public Company update(Long id, CompanyDTO dto) {
        if (id == null) {
            throw new BusinessException("Company ID is required for update.");
        }

        Company existing = findById(id);
        if (dto.cnpj() != null && !dto.cnpj().isBlank()) {
            if (!dto.cnpj().equals(existing.getCnpj())) {
                if (companyRepository.existsByCnpj(dto.cnpj())) {
                    throw new BusinessException(
                            "The CNPJ " + dto.cnpj() + " is already in use by another company.");
                }
                existing.setCnpj(dto.cnpj());
            }
        }
        if (dto.name() != null) existing.setName(dto.name());
        if (dto.cep() != null) existing.setCep(dto.cep());
        if (dto.addressNumber() != null) existing.setAddressNumber(dto.addressNumber());

        return companyRepository.save(Objects.requireNonNull(existing));
    }

    @Transactional
    public void deleteById(Long id) {
        Company company = findById(id);
        companyRepository.delete(Objects.requireNonNull(company));
    }
}
