package br.rafaeros.aura.modules.ble.model;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.core.security.CryptoConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant; // Alterado para Instant

@Entity
@Table(name = "ble_locations")
@Getter
@Setter
@NoArgsConstructor
public class BleLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ble_device_id", nullable = false)
    private BleDevice bleDevice;

    @Convert(converter = CryptoConverter.class)
    @Column(nullable = false)
    private Double latitude;

    @Convert(converter = CryptoConverter.class)
    @Column(nullable = false)
    private Double longitude;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "accuracy")
    private Integer accuracy;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "confidence")
    private Integer confidence;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "movement_status") 
    private Integer movementStatus; 

    @Convert(converter = CryptoConverter.class)
    @Column(name = "battery_status")
    private String batteryStatus;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "published_at")
    private Instant publishedAt;
}