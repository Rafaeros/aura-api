package br.rafaeros.aura.modules.company.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_integration_settings")
public class CompanySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    @JsonIgnore
    private Company company;

    @Column(name = "everynet_access_token")
    private String everynetAccessToken;

    @Column(name = "mqtt_host")
    private String mqttHost;

    @Column(name = "mqtt_port")
    private Integer mqttPort;

    @Column(name = "mqtt_username")
    private String mqttUsername;

    @Column(name = "mqtt_password")
    private String mqttPassword;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public CompanySettings() {
    }

    public CompanySettings(Long id, Company company, String everynetAccessToken, String mqttHost,
            Integer mqttPort, String mqttUsername, String mqttPassword, OffsetDateTime updatedAt) {
        this.id = id;
        this.company = company;
        this.everynetAccessToken = everynetAccessToken;
        this.mqttHost = mqttHost;
        this.mqttPort = mqttPort;
        this.mqttUsername = mqttUsername;
        this.mqttPassword = mqttPassword;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getEverynetAccessToken() {
        return everynetAccessToken;
    }

    public void setEverynetAccessToken(String everynetAccessToken) {
        this.everynetAccessToken = everynetAccessToken;
    }

    public String getMqttHost() {
        return mqttHost;
    }

    public void setMqttHost(String mqttHost) {
        this.mqttHost = mqttHost;
    }

    public Integer getMqttPort() {
        return mqttPort;
    }

    public void setMqttPort(Integer mqttPort) {
        this.mqttPort = mqttPort;
    }

    public String getMqttUsername() {
        return mqttUsername;
    }

    public void setMqttUsername(String mqttUsername) {
        this.mqttUsername = mqttUsername;
    }

    public String getMqttPassword() {
        return mqttPassword;
    }

    public void setMqttPassword(String mqttPassword) {
        this.mqttPassword = mqttPassword;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
