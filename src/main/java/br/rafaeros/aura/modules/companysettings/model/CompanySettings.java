package br.rafaeros.aura.modules.companysettings.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.core.security.CryptoConverter;
import br.rafaeros.aura.modules.company.model.Company;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "company_settings")
public class CompanySettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    @JsonIgnore
    private Company company;

    @Convert(converter = CryptoConverter.class)
    private String everynetAccessToken;

    private String mqttHost;

    private Integer mqttPort;

    private String mqttUsername;

    @Convert(converter = CryptoConverter.class)
    private String mqttPassword;

    private String subscribeTopic;

    private String publishTopic;
}