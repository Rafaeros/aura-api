package br.rafaeros.aura.modules.company.controller;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.core.dto.ApiResponse;
import br.rafaeros.aura.modules.company.controller.dto.CompanyDTO;
import br.rafaeros.aura.modules.company.service.CompanyService;
import br.rafaeros.aura.modules.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyDTO.Response>> create(
            @Valid @RequestBody CompanyDTO.CreateRequest request) {
        CompanyDTO.Response company = companyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Empresa criada com sucesso.", company));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<CompanyDTO.Response>>> getAll(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<CompanyDTO.Response> companies = companyService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Empresas listadas com sucesso.", companies));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@companySecurity.canAccessCompany(#id, #user)")
    public ResponseEntity<ApiResponse<CompanyDTO.Response>> getById(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        CompanyDTO.Response company = companyService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Empresa listada com sucesso.", company));
    }

    @GetMapping({"/me", "/current"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CompanyDTO.Response>> getMyCompany(@AuthenticationPrincipal User user) {
        if (user.getCompany() == null) {
            throw new br.rafaeros.aura.core.exception.ResourceNotFoundException("Usuário não possui empresa vinculada");
        }
        CompanyDTO.Response company = companyService.findById(user.getCompany().getId());
        return ResponseEntity.ok(ApiResponse.success("Empresa logada listada com sucesso.", company));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyDTO.Response>> update(
            @PathVariable Long id, @Valid @RequestBody CompanyDTO.UpdateRequest request) {
        CompanyDTO.Response updated = companyService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Empresa atualizada com sucesso.", updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(companyService.toggleActive(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        companyService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Empresa deletada com sucesso."));
    }
}