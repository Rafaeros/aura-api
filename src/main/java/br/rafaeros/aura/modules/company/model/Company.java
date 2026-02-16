package br.rafaeros.aura.modules.company.model;

import java.util.List;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.modules.companysettings.model.CompanySettings;
import br.rafaeros.aura.modules.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 14, unique = true)
    private String cnpj;

    @Column(length = 8)
    private String cep;

    private Integer addressNumber;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<User> users;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CompanySettings settings;
}