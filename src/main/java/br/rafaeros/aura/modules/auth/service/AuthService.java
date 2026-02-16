package br.rafaeros.aura.modules.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.exception.BusinessException;
import br.rafaeros.aura.core.exception.ResourceNotFoundException;
import br.rafaeros.aura.core.security.JwtService;
import br.rafaeros.aura.modules.auth.controller.dto.AuthDTO;
import br.rafaeros.aura.modules.companysettings.repository.CompanySettingsRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CompanySettingsRepository companySettingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthDTO.Response activateAccount(AuthDTO.FirstAccessRequest request, User user) {
        if (user == null) {
            throw new BusinessException("Usuário não identificado. Envie o token de acesso.");
        }

        validateUserStatus(user);
        validateActivation(request, user);

        user.setFirstAccess(false);
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setActive(true);

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        boolean isSettingsConfigured = checkSettingsConfigured(user);

        return new AuthDTO.Response(token, isSettingsConfigured, user.isFirstAccess());
    }

    public AuthDTO.Response login(AuthDTO.Request request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (DisabledException e) {
            throw new BusinessException("Sua conta ou empresa estão desativadas. Contate o suporte.");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("E-mail ou senha inválidos.");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        validateUserStatus(user);

        String token = jwtService.generateToken(user);
        boolean isSettingsConfigured = checkSettingsConfigured(user);

        return new AuthDTO.Response(token, isSettingsConfigured, user.isFirstAccess());
    }

    private void validateUserStatus(User user) {
        if (!user.isActive()) {
            throw new BusinessException("Usuário inativo. Contate o administrador.");
        }
        if (user.getCompany() != null && !user.getCompany().isActive()) {
            throw new BusinessException("O acesso da sua empresa está suspenso.");
        }
    }

    private boolean checkSettingsConfigured(User user) {
        if (user.getRole() == Role.OWNER && user.getCompany() != null) {
            return companySettingsRepository.existsByCompanyId(user.getCompany().getId());
        }
        return true;
    }

    private void validateActivation(AuthDTO.FirstAccessRequest request, User user) {
        if (!user.isFirstAccess()) {
            throw new BusinessException("Esta conta já foi ativada.");
        }

        if (!passwordEncoder.matches(request.tempPassword(), user.getPassword())) {
            throw new BadCredentialsException("A senha temporária está incorreta.");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new BusinessException("A nova senha e a confirmação não coincidem.");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException("A nova senha não pode ser igual à senha temporária.");
        }
    }
}