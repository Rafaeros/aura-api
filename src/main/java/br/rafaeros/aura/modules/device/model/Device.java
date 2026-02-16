package br.rafaeros.aura.modules.device.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.modules.device.client.dto.EverynetDevice;
import br.rafaeros.aura.modules.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "device")
@NoArgsConstructor
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( nullable = false, unique = true)
    private String devEui;

    @Column( nullable = false)
    private String devAddr;

    @Column(nullable = false)
    private String appEui;

    @Column(nullable = false)
    private String nwksKey;

    @Column( nullable = false)
    private String appsKey;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserDevice> usersLink = new ArrayList<>();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "device_tags", joinColumns = @JoinColumn(name = "device_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<DeviceTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DevicePosition> positions = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceFeature> features;

    public static Device createFromEverynet(EverynetDevice externalData) {
        Device device = new Device();

        device.setDevEui(externalData.getDevEui());
        device.setDevAddr(externalData.getDevAddr());
        device.setAppEui(externalData.getAppEui());
        device.setNwksKey(externalData.getNwkskey());
        device.setAppsKey(externalData.getAppskey());

        return device;
    }

    public void addUser(User user, String customName) {
        UserDevice link = new UserDevice(user, this, customName);
        this.usersLink.add(link);
    }

    public void addTag(DeviceTag tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
            tag.getDevices().add(this);
        }
    }

    public void addPosition(DevicePosition position) {
        if (this.positions == null) {
            this.positions = new ArrayList<>();
        }
        this.positions.add(position);
        position.setDevice(this);
    }
}
