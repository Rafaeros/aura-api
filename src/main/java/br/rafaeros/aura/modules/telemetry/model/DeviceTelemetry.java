package br.rafaeros.aura.modules.telemetry.model;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.telemetry.model.enums.TelemetrySource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "device_telemetry")
public class DeviceTelemetry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private TelemetrySource source;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "jsonb")
    private String payload;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id")
    private Device device;
}