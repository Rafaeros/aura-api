package br.rafaeros.aura.modules.company.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.rafaeros.aura.core.model.BaseEntity;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@Entity
@Table(name = "company")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The company name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "The CNPJ is required")
    @Size(min = 14, max = 14, message = "The CNPJ must have 14 digits")
    @Column(nullable = false, length = 14, unique = true)
    private String cnpj;

    @Size(min = 8, max = 8, message = "The CEP must have 8 digits")
    @Column(length = 8)
    private String cep;

    @Column(name = "address_number")
    private Integer addressNumber;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)

    @JsonIgnore
    private CompanySettings settings;

    public Company() {
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
