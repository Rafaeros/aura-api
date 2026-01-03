package br.rafaeros.aura.modules.company.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import br.rafaeros.aura.modules.company.controller.dto.CompanyDTO;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.service.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Company> create(@Valid @RequestBody CompanyDTO company) {
        Company saved = companyService.create(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.belongsToCompany(authentication, #id)")
    public ResponseEntity<Company> findById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Company>> findAll() {
        return ResponseEntity.ok(companyService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Company> update(@PathVariable Long id, @Valid @RequestBody CompanyDTO company) {
        Company updated = companyService.update(id, company);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        companyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}