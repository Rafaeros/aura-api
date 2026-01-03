package br.rafaeros.aura.modules.telemetry.model;

import java.time.OffsetDateTime;

import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.device.model.Device;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mqtt_log")
public class MqttLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "json")
    private String params;

    @Column(columnDefinition = "json")
    private String meta;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;
}
