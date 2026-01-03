package br.rafaeros.aura.modules.company.model;

import br.rafaeros.aura.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.List;

@Entity
@Table(name = "company")
public class Company {

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

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @OneToOne(
            mappedBy = "company",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonIgnore
    private CompanySettings integrationSettings;

    public Company() {}

    public Company(String name, String cnpj, String cep, Integer addressNumber) {
        this.name = name;
        this.cnpj = cnpj;
        this.cep = cep;
        this.addressNumber = addressNumber;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Integer getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(Integer addressNumber) {
        this.addressNumber = addressNumber;
    }

    public List<User> getUsers() {
        return users;
    }

    public CompanySettings getIntegrationSettings() {
        return integrationSettings;
    }

    public void setIntegrationSettings(CompanySettings integrationSettings) {
        this.integrationSettings = integrationSettings;

        if (integrationSettings != null) {
            integrationSettings.setCompany(this);
        }
    }
}
