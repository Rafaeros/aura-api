package br.rafaeros.aura.modules.user.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.controller.dto.CompanyResponseDTO;
import br.rafaeros.aura.modules.company.controller.dto.CompanySettingsResponseDTO;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import br.rafaeros.aura.modules.user.controller.dto.UserCreateDTO;
import br.rafaeros.aura.modules.user.controller.dto.UserProfileDTO;
import br.rafaeros.aura.modules.user.controller.dto.UserResponseDTO;
import br.rafaeros.aura.modules.user.controller.dto.UserUpdateDTO;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String DEFAULT_PASSWORD = "mudar@123";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    @Transactional
    public UserResponseDTO create(UserCreateDTO dto) {
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
        newUser.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));

        User saved = repository.save(newUser);

        return UserResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        Pageable safePageable = Objects.requireNonNull(pageable);
        return repository.findAll(safePageable).map(UserResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(long id) {
        User user = findEntityById(id);
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public UserProfileDTO findUserProfile(Authentication authentication) {
        String email = authentication.getName();

        boolean isAdminOrOwner = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_OWNER"));

        User user = repository.findProfileByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        UserProfileDTO dto = UserProfileDTO.fromEntity(user);

        if (!isAdminOrOwner && dto.company() != null && dto.company().settings() != null) {
            CompanySettingsResponseDTO maskedSettings = dto.company().settings().maskSecrets();
            CompanyResponseDTO maskedCompany = new CompanyResponseDTO(
                    dto.company().id(), dto.company().name(), dto.company().cnpj(),
                    dto.company().cep(), dto.company().addressNumber(), maskedSettings);
            dto = new UserProfileDTO(dto.id(), dto.username(), dto.email(), maskedCompany);
        }

        return dto;
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        if (id == null)
            throw new BusinessException("User ID is required.");

        User user = findEntityById(id);

        if (dto.email() != null && !dto.email().isBlank() && !dto.email().equals(user.getEmail())) {
            if (repository.existsByEmail(dto.email())) {
                throw new BusinessException("Email already in use.");
            }
            user.setEmail(dto.email());
        }

        if (dto.username() != null && !dto.username().isBlank() && !dto.username().equals(user.getUsername())) {
            if (repository.existsByUsername(dto.username())) {
                throw new BusinessException("Username already taken.");
            }
            user.setUsername(dto.username());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        User updated = repository.save(Objects.requireNonNull(user));
        return UserResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void deleteById(long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. User not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    private User findEntityById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

}