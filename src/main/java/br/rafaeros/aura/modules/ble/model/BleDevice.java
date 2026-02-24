package br.rafaeros.aura.modules.ble.model;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.core.security.CryptoConverter;
import br.rafaeros.aura.modules.company.model.Company;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ble_devices")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("is_active = true")
@SQLDelete(sql = "UPDATE ble_devices SET is_active = false WHERE id = ?")
public class BleDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "hashed_public_key", nullable = false, unique = true)
    private String hashedPublicKey;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "private_key_base64")
    private String privateKeyBase64;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}