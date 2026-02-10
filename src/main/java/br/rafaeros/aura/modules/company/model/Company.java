package br.rafaeros.aura.modules.company.model;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "company")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da empresa é obrigatório.")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "O CNPJ é obrigatório.")
    @Size(min = 14, max = 14, message = "O CNPJ deve ter exatos 14 dígitos.")
    @Column(nullable = false, length = 14, unique = true)
    private String cnpj;

    @Size(min = 8, max = 8, message = "O CEP deve ter 8 dígitos.")
    @Column(length = 8)
    private String cep;

    private Integer addressNumber;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private CompanySettings settings;
}