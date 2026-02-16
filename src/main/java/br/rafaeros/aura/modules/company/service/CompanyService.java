package br.rafaeros.aura.modules.company.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.controller.dto.CompanyDTO;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyDTO.Response create(CompanyDTO.CreateRequest dto) {
        if (companyRepository.existsByCnpj(dto.cnpj())) {
            throw new BusinessException("A Empresa com CNPJ " + dto.cnpj() + " já existe.");
        }
        Company company = new Company();
        company.setName(dto.name());
        company.setCnpj(dto.cnpj());

        if (dto.cep() != null) {
            company.setCep(dto.cep());
        }

        if (dto.addressNumber() != null) {
            company.setAddressNumber(dto.addressNumber());
        }

        return CompanyDTO.Response.fromEntity(companyRepository.save(company));
    }

    @Transactional(readOnly = true)
    public Page<CompanyDTO.Response> findAll(Pageable pageable) {
        Pageable safePageable = Objects.requireNonNull(pageable);
        return companyRepository.findAll(safePageable)
                .map(CompanyDTO.Response::fromEntity);
    }

    @Transactional(readOnly = true)
    public CompanyDTO.Response findById(Long id) {
        Company company = findByIdInternal(id);

        return CompanyDTO.Response.fromEntity(company);
    }

    @Transactional
    public CompanyDTO.Response update(Long id, CompanyDTO.UpdateRequest dto) {
        Company existing = findByIdInternal(id);

        if (dto.cnpj() != null && !dto.cnpj().isBlank()) {
            if (!dto.cnpj().equals(existing.getCnpj())) {
                if (companyRepository.existsByCnpj(dto.cnpj())) {
                    throw new BusinessException("O CNPJ " + dto.cnpj() + " já está em uso.");
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

        return CompanyDTO.Response.fromEntity(updated);
    }

    @Transactional
    public String toggleActive(Long id) {
        Company company = findByIdInternal(id);
        company.setActive(!company.isActive());
        Company saved = companyRepository.save(Objects.requireNonNull(company));
        return "Empresa " + (saved.isActive() ? "ativada" : "desativada") + " com sucesso.";
    }

    @Transactional
    public void deleteById(Long id) {
        Company company = findByIdInternal(id);
        companyRepository.delete(Objects.requireNonNull(company));
    }

    public Company findByIdInternal(Long id) {
        if (id == null)
            throw new BusinessException("O ID da empresa é obrigatório.");
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));
    }
}