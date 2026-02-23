package br.rafaeros.aura.modules.auth.model;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.core.security.CryptoConverter; // Seu conversor
import br.rafaeros.aura.modules.company.model.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_keys")
@Getter
@Setter
@NoArgsConstructor
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "api_key", nullable = false, unique = true, updatable = false)
    private String key;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String authorities; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}