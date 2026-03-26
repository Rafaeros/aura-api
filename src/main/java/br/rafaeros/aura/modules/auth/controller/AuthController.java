package br.rafaeros.aura.modules.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.modules.auth.controller.dto.AuthDTO;
import br.rafaeros.aura.modules.auth.service.AuthService;
import br.rafaeros.aura.modules.user.model.User;
import jakarta.validation.Valid;


import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/activate-account")
    public ResponseEntity<ApiResponse<AuthDTO.Response>> activateAccount(
            @RequestBody @Valid AuthDTO.FirstAccessRequest request, @AuthenticationPrincipal User user) {
        AuthDTO.Response response = authService.activateAccount(request, user);
        return ResponseEntity.ok(ApiResponse.success("Conta ativada com sucesso.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTO.Response>> login(@RequestBody @Valid AuthDTO.Request request) {
        AuthDTO.Response response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso.", response));
    }
}