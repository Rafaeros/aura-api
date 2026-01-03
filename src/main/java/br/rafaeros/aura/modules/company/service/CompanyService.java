package br.rafaeros.aura.modules.company.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.modules.company.controller.dto.CompanyDTO;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public Company create(CompanyDTO dto) {
        if (companyRepository.existsByCnpj(dto.cnpj())) {
            throw new IllegalArgumentException("Company with cnpj " + dto.cnpj() + " already exists");
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
            throw new IllegalArgumentException("Company id must be provided");
        }
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
    }

    @Transactional
    public Company update(Long id, Company company) {
        Company existing = findById(id);

        existing.setName(company.getName());
        existing.setCnpj(company.getCnpj());
        existing.setCep(company.getCep());
        existing.setAddressNumber(company.getAddressNumber());

        return companyRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        Company company = findById(id);
        if (company == null) {
            throw new EntityNotFoundException("Company not found");
        }
        companyRepository.delete(company);
    }
}
