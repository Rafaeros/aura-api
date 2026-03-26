package br.rafaeros.aura.modules.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.modules.user.controller.dto.UserDTO;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO.Response>> create(@Valid @RequestBody UserDTO.CreateRequest user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Usuário criado com sucesso.", userService.create(user)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO.Response>>> getAll(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Usuários listados com sucesso.", userService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.canViewProfile(#id, #user)")
    public ResponseEntity<ApiResponse<UserDTO.ProfileResponse>> getById(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Usuário listado com sucesso.", userService.findProfileById(id)));
    }

    @GetMapping({"/me", "/current"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDTO.ProfileResponse>> getMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Usuário logado listado com sucesso.", userService.findProfileById(user.getId())));
    }

    @PatchMapping("/{id}/change-password")
    @PreAuthorize("@userSecurity.canChangePassword(#id, #user)")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Long id,
            @Valid @RequestBody UserDTO.ChangePasswordRequest request, @AuthenticationPrincipal User user) {
        userService.changePassword(id, request);
        return ResponseEntity.ok(ApiResponse.success("Senha alterada com sucesso."));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Usuário deletado com sucesso."));
    }

}