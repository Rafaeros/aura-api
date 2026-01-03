package br.rafaeros.aura.modules.device.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import lombok.Setter;

@Entity
@Table(name = "device")
@Getter
@Setter
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("name")
    @Column(name = "name")
    private String name;

    @JsonProperty("dev_eui")
    @Column(name = "dev_eui", nullable = false, unique = true)
    private String devEui;

    @Column(name = "dev_addr", nullable = false)
    private String devAddr;

    @Column(name = "app_eui", nullable = false)
    private String appEui;

    @Column(name = "nwks_key", nullable = false)
    private String nwksKey;

    @Column(name = "apps_key", nullable = false)
    private String appsKey;

    @ManyToMany(mappedBy = "devices")
    @JsonIgnore
    private List<User> users;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "device_tags", joinColumns = @JoinColumn(name = "device_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<DeviceTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DevicePosition> positions = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceFeature> features;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Device() {
    }

    public Device(Long id, String name, String devEui, String devAddr, String appEui, String nwksKey, String appsKey,
            List<User> users, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.devEui = devEui;
        this.devAddr = devAddr;
        this.appEui = appEui;
        this.nwksKey = nwksKey;
        this.appsKey = appsKey;
        this.users = users;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Device createFromEverynet(EverynetDevice externalData, String nameInput) {
        Device device = new Device();

        device.setDevEui(externalData.getDevEui());

        if (nameInput != null && !nameInput.trim().isEmpty()) {
            device.setName(nameInput);
        } else {
            device.setName("Device " + externalData.getDevEui());
        }

        device.setDevAddr(externalData.getDevAddr());
        device.setAppEui(externalData.getAppEui());

        device.setNwksKey(externalData.getNwkskey());
        device.setAppsKey(externalData.getAppskey());

        OffsetDateTime now = OffsetDateTime.now();
        device.setCreatedAt(now);
        device.setUpdatedAt(now);

        return device;
    }

    public void addUser(User user) {
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        if (!this.users.contains(user)) {
            this.users.add(user);
        }
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
