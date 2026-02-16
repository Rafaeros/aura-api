package br.rafaeros.aura.modules.device.model;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.modules.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_device")
public class UserDevice extends BaseEntity {

    @EmbeddedId
    private UserDeviceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("deviceId")
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(nullable = false)
    private String name;

    public UserDevice(User user, Device device, String name) {
        this.id = new UserDeviceId(user.getId(), device.getId());
        this.user = user;
        this.device = device;
        this.name = name;
    }

}
