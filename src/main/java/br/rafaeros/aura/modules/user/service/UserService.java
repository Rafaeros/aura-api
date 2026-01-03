package br.rafaeros.aura.modules.user.service;

import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import br.rafaeros.aura.modules.user.controller.dto.UserCreateDTO;
import br.rafaeros.aura.modules.user.controller.dto.UserUpdateDTO;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(UserCreateDTO dto) {
        if (repository.existsByUsername(dto.username())) {
            throw new RuntimeException("User already exists");
        }

        Long companyId = dto.companyId();
        if (companyId == null) {
            throw new IllegalArgumentException("The company ID must be provided");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        User newUser = new User();
        newUser.setUsername(dto.username());
        newUser.setRole(dto.role());
        newUser.setCompany(company);
        newUser.setPassword(passwordEncoder.encode(dto.password()));

        return repository.save(newUser);
    }

    @Transactional(readOnly = true)
    public User findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User update(Long id, UserUpdateDTO dto) {
        Long validId = java.util.Objects.requireNonNull(id, "ID is required");

        User user = repository.findById(validId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (dto.username() != null && !dto.username().isBlank()) {
            user.setUsername(dto.username());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        return repository.save(java.util.Objects.requireNonNull(user));
    }

    @Transactional
    public void deleteById(long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        repository.deleteById(id);
    }
}
