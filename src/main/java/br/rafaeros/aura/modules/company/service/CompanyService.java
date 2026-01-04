package br.rafaeros.aura.modules.company.service;

import java.util.List;
import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.controller.dto.CompanyDTO;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
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
    public Company findById(Long id, String email) {
        if (id == null)
            throw new BusinessException("Company ID is required.");

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean belongsToCompany = user.getCompany() != null && user.getCompany().getId().equals(id);

        if (!isAdmin && !belongsToCompany) {
            throw new AccessDeniedException("Access denied: You do not belong to this company.");
        }

        return company;
    }

    private Company findByIdInternal(Long id) {
        if (id == null)
            throw new BusinessException("Company ID is required.");
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
    }

    @Transactional
    public Company update(Long id, CompanyDTO dto) {
        if (id == null)
            throw new BusinessException("Company ID is required.");

        Company existing = findByIdInternal(id);

        if (dto.cnpj() != null && !dto.cnpj().isBlank()) {
            if (!dto.cnpj().equals(existing.getCnpj())) {
                if (companyRepository.existsByCnpj(dto.cnpj())) {
                    throw new BusinessException("The CNPJ " + dto.cnpj() + " is already in use.");
                }
                existing.setCnpj(dto.cnpj());
            }
        }
        if (dto.name() != null)
            existing.setName(dto.name());
        if (dto.cep() != null)
            existing.setCep(dto.cep());
        if (dto.addressNumber() != null)
            existing.setAddressNumber(dto.addressNumber());

        return companyRepository.save(Objects.requireNonNull(existing));
    }

    @Transactional
    public void deleteById(Long id) {
        Company company = findByIdInternal(id);
        companyRepository.delete(Objects.requireNonNull(company));
    }
}