package br.rafaeros.aura.modules.ble.model;

import br.rafaeros.aura.core.model.BaseEntity;
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

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "accuracy")
    private Integer accuracy;

    @Column(name = "confidence")
    private Integer confidence;

    @Column(name = "movement_status") 
    private Integer movementStatus; 

    @Column(name = "battery_status")
    private String batteryStatus;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "published_at")
    private Instant publishedAt;
}