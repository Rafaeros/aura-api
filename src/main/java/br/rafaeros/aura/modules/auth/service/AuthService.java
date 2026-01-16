package br.rafaeros.aura.modules.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.core.security.JwtService;
import br.rafaeros.aura.modules.auth.controller.dto.AuthRequestDTO;
import br.rafaeros.aura.modules.auth.controller.dto.AuthResponseDTO;
import br.rafaeros.aura.modules.auth.controller.dto.FirstAccessRequestDTO;
import br.rafaeros.aura.modules.company.repository.CompanySettingsRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanySettingsRepository companySettingsRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponseDTO activateAccount(FirstAccessRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateActivation(user, request);

        user.setFirstAccess(false);
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        boolean isSettingsConfigured = checkSettingsConfigured(user);

        return new AuthResponseDTO(token, isSettingsConfigured, user.isFirstAccess());
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!validateUserPassword(user, request.password())) {
            throw new BadCredentialsException("Invalid password.");
        }

        String token = jwtService.generateToken(user);
        boolean isSettingsConfigured = checkSettingsConfigured(user);

        return new AuthResponseDTO(token, isSettingsConfigured, user.isFirstAccess());
    }

    private boolean validateUserPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    private boolean checkSettingsConfigured(User user) {
        if (user.getRole() == Role.OWNER) {
            if (user.getCompany() != null) {
                return companySettingsRepository.existsByCompanyId(user.getCompany().getId());
            }
            return false;
        }
        return true;
    }

    private void validateActivation(User user, FirstAccessRequestDTO request) {
        if (!user.isFirstAccess()) {
            throw new BusinessException("This account is already activated.");
        }

        if (!passwordEncoder.matches(request.tempPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid temporary password.");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new IllegalArgumentException("The new password and confirmation password do not match.");
        }

        if (request.newPassword().equals(request.tempPassword())) {
            throw new IllegalArgumentException("The new password cannot be the same as the temporary password.");
        }
    }
}