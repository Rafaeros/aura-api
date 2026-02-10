package br.rafaeros.aura.modules.company.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.controller.dto.CompanyRequestDTO;
import br.rafaeros.aura.modules.company.controller.dto.CompanyResponseDTO;
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
    public CompanyResponseDTO create(CompanyRequestDTO dto) {
        if (companyRepository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("Company with CNPJ " + dto.cnpj() + " already exists.");
        }
        Company company = new Company();
        company.setName(dto.name());
        company.setCnpj(dto.cnpj());
        company.setCep(dto.cep());
        company.setAddressNumber(dto.addressNumber());

        Company saved = companyRepository.save(company);

        return CompanyResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponseDTO> findAll(Pageable pageable) {
        Pageable safePageable = Objects.requireNonNull(pageable);
        return companyRepository.findAll(safePageable)
                .map(CompanyResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO findById(Long id, String email) {
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

        return CompanyResponseDTO.fromEntity(company);
    }

    private Company findByIdInternal(Long id) {
        if (id == null)
            throw new BusinessException("Company ID is required.");
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
    }

    @Transactional
    public CompanyResponseDTO update(Long id, CompanyRequestDTO dto) {
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

        Company updated = companyRepository.save(Objects.requireNonNull(existing));

        return CompanyResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void toggleActive(Long id) {
        Company company = findByIdInternal(id);
        company.setActive(!company.isActive());
        companyRepository.save(Objects.requireNonNull(company));
    }

    @Transactional
    public void deleteById(Long id) {
        Company company = findByIdInternal(id);
        companyRepository.delete(Objects.requireNonNull(company));
    }
}