package br.rafaeros.aura.modules.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.modules.user.controller.dto.AuthRequest;
import br.rafaeros.aura.modules.user.controller.dto.AuthResponse;
import br.rafaeros.aura.modules.user.controller.dto.FirstAccessRequest;
import br.rafaeros.aura.modules.user.service.AuthService; // Importar o Service
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/activate-account")
    public ResponseEntity<AuthResponse> activateAccount(@RequestBody @Valid FirstAccessRequest request) {
        AuthResponse response = authService.activateAccount(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}