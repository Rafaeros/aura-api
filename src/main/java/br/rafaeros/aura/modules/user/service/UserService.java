package br.rafaeros.aura.modules.user.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import br.rafaeros.aura.modules.user.controller.dto.UserDTO;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String DEFAULT_PASSWORD = "mudar@123";

    @Transactional
    public UserDTO.Response create(UserDTO.CreateRequest request) {
        boolean existsOpt = userRepository.existsByEmail(request.email());
        if (existsOpt) {
            throw new BusinessException("E-mail já cadastrado.");
        }
        Company company = companyRepository.findById(Objects.requireNonNull(request.companyId()))
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada."));
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setRole(request.role());
        user.setCompany(company);
        User savedUser = userRepository.save(user);
        return UserDTO.Response.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO.Response> findAll(Pageable pageable) {
        Pageable safePageable = Objects.requireNonNull(pageable);
        return userRepository.findAll(safePageable).map(UserDTO.Response::fromEntity);
    }

    @Transactional(readOnly = true)
    public UserDTO.ProfileResponse findProfileById(Long id) {
        User user = userRepository.findProfileById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        return UserDTO.ProfileResponse.fromEntity(user);
    }

    @Transactional
    public UserDTO.Response changePassword(Long id, UserDTO.ChangePasswordRequest request) {

        if (request.newPassword().equals(request.currentPassword())) {
            throw new BusinessException("A nova senha não pode ser igual à senha atual.");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("A nova senha e a confirmação não coincidem.");
        }

        User user = userRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário nao encontrado."));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException("Senha atual incorreta.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        User savedUser = userRepository.save(user);
        return UserDTO.Response.fromEntity(savedUser);
    }


    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        userRepository.delete(Objects.requireNonNull(user));
    }

    // Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

}