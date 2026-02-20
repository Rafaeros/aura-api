package br.rafaeros.aura.modules.findmy.model;

import br.rafaeros.aura.core.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "findmy_locations")
@Getter
@Setter
@NoArgsConstructor
public class FindMyLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "findmy_device_id", nullable = false)
    private FindMyDevice findMyDevice;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "accuracy")
    private Integer accuracy; // Precisão do GPS do iPhone que capturou o sinal

    @Column(name = "confidence")
    private Integer confidence; // Nível de confiança retornado pela Apple

    @Column(name = "battery_status")
    private String batteryStatus; // Ex: "FULL", "MEDIUM", "LOW"

    // Quando a tag enviou o sinal / O iPhone capturou
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Quando o relatório foi publicado nos servidores da Apple
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}