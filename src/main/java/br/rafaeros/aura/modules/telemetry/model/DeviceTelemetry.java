package br.rafaeros.aura.modules.telemetry.model;

import org.hibernate.annotations.JdbcTypeCode; // Importante!
import org.hibernate.type.SqlTypes; // Importante!

import com.fasterxml.jackson.annotation.JsonIgnore;
import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.telemetry.model.enums.TelemetrySource;
import jakarta.persistence.*;
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id")
    @JsonIgnore
    private Device device;
}