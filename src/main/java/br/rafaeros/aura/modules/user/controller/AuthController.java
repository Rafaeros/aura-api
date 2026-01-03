package br.rafaeros.aura.modules.user.controller;

import br.rafaeros.aura.core.security.CustomUserDetailsService;
import br.rafaeros.aura.core.security.JwtService;
import br.rafaeros.aura.modules.user.controller.dto.AuthRequest;
import br.rafaeros.aura.modules.user.controller.dto.AuthResponse;
import br.rafaeros.aura.modules.user.model.User;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        User user = userDetailsService.loadDomainUser(request.getUsername());

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getCompany().getId(),
                user.getRole().name());

        return new AuthResponse(token);
    }
}
