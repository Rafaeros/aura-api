package br.rafaeros.aura.modules.user.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import br.rafaeros.aura.modules.user.controller.dto.UserCreateDTO;
import br.rafaeros.aura.modules.user.controller.dto.UserProfileDTO;
import br.rafaeros.aura.modules.user.controller.dto.UserUpdateDTO;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(UserCreateDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new BusinessException("Email '" + dto.email() + "' is already in use.");
        }

        if (repository.existsByUsername(dto.username())) {
            throw new BusinessException("Username '" + dto.username() + "' is already taken.");
        }

        Long companyId = dto.companyId();
        if (companyId == null) {
            throw new BusinessException("Company ID is required.");
        }
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        User newUser = new User();
        newUser.setEmail(dto.email());
        newUser.setUsername(dto.username());
        newUser.setRole(dto.role());
        newUser.setCompany(company);
        newUser.setPassword(passwordEncoder.encode(dto.password()));

        return repository.save(newUser);
    }

    @Transactional(readOnly = true)
    public Iterable<User> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public UserProfileDTO findUserProfile(String email) {
        var user = repository.findyProfileByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserProfileDTO.fromEntity(user);
    }

    @Transactional
    public User update(Long id, UserUpdateDTO dto) {
        if (id == null)
            throw new BusinessException("User ID is required.");

        User user = findById(id);

        if (dto.email() != null && !dto.email().isBlank()) {
            if (!dto.email().equals(user.getEmail())) {
                if (repository.existsByEmail(dto.email())) {
                    throw new BusinessException("The email '" + dto.email() + "' is already in use.");
                }
                user.setEmail(dto.email());
            }
        }

        if (dto.username() != null && !dto.username().isBlank()) {
            if (!dto.username().equals(user.getUsername())) {
                if (repository.existsByUsername(dto.username())) {
                    throw new BusinessException("The username '" + dto.username() + "' is already taken.");
                }
                user.setUsername(dto.username());
            }
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        return repository.save(Objects.requireNonNull(user));
    }

    @Transactional
    public void deleteById(long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. User not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}