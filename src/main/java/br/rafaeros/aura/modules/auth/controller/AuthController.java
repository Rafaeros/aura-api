package br.rafaeros.aura.modules.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.modules.auth.controller.dto.AuthRequestDTO;
import br.rafaeros.aura.modules.auth.controller.dto.AuthResponseDTO;
import br.rafaeros.aura.modules.auth.controller.dto.FirstAccessRequestDTO;
import br.rafaeros.aura.modules.auth.service.AuthService;
import br.rafaeros.aura.modules.user.model.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/activate-account")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> activateAccount(@AuthenticationPrincipal User user,
            @RequestBody @Valid FirstAccessRequestDTO request) {
        AuthResponseDTO response = authService.activateAccount(user, request);
        return ResponseEntity.ok(ApiResponse.success("Conta ativada com sucesso.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody @Valid AuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso.", response));
    }
}