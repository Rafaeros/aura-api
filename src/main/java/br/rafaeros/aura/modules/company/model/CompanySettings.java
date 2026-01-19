package br.rafaeros.aura.modules.company.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.core.security.CryptoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @Column(name = "everynet_access_token")
    @Convert(converter = CryptoConverter.class)
    private String everynetAccessToken;

    @Column(name = "mqtt_host")
    private String mqttHost;

    @Column(name = "mqtt_port")
    private Integer mqttPort;

    @Column(name = "mqtt_username")
    private String mqttUsername;

    @Column(name = "mqtt_password")
    @Convert(converter = CryptoConverter.class)
    private String mqttPassword;

    @Column(name="subscribe_topic")
    private String subscribeTopic;

    @Column(name="publish_topic")
    private String publishTopic;

    public CompanySettings() {
    }
}
