package br.rafaeros.aura.modules.findmy.model;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.modules.company.model.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "findmy_devices")
@Getter
@Setter
@NoArgsConstructor
public class FindMyDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "hashed_public_key", nullable = false, unique = true)
    private String hashedPublicKey; 

    @Column(name = "private_key_base64")
    private String privateKeyBase64;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}