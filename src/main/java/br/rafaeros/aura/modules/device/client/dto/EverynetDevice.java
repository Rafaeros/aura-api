package br.rafaeros.aura.modules.device.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EverynetDevice {

    private String activation;
    private Adr adr;

    @JsonProperty("app_eui")
    private String appEui;

    @JsonProperty("app_key")
    private String appKey;

    @JsonProperty("appskey")
    private String appskey;

    private String band;

    @JsonProperty("block_downlink")
    private Boolean blockDownlink;

    @JsonProperty("block_uplink")
    private Boolean blockUplink;

    @JsonProperty("counter_down")
    private Integer counterDown;

    @JsonProperty("counter_up")
    private Integer counterUp;

    @JsonProperty("counters_size")
    private Integer countersSize;

    @JsonProperty("created_at")
    private Double createdAt;

    @JsonProperty("dev_addr")
    private String devAddr;

    @JsonProperty("dev_class")
    private String devClass;

    @JsonProperty("dev_eui")
    private String devEui;

    private String encryption;
    private Geolocation geolocation;

    @JsonProperty("last_activity")
    private Double lastActivity;

    @JsonProperty("last_join")
    private Double lastJoin;

    private Boolean locked;

    @JsonProperty("lorawan_version")
    private String lorawanVersion;

    private Boolean multicast;

    @JsonProperty("nwkskey")
    private String nwkskey;

    private Rx1 rx1;
    private Rx2 rx2;

    @JsonProperty("strict_counter")
    private Boolean strictCounter;

    private List<String> tags;

    public EverynetDevice() {}

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public Adr getAdr() {
        return adr;
    }

    public void setAdr(Adr adr) {
        this.adr = adr;
    }

    public String getAppEui() {
        return appEui;
    }

    public void setAppEui(String appEui) {
        this.appEui = appEui;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppskey() {
        return appskey;
    }

    public void setAppskey(String appskey) {
        this.appskey = appskey;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public Boolean getBlockDownlink() {
        return blockDownlink;
    }

    public void setBlockDownlink(Boolean blockDownlink) {
        this.blockDownlink = blockDownlink;
    }

    public Boolean getBlockUplink() {
        return blockUplink;
    }

    public void setBlockUplink(Boolean blockUplink) {
        this.blockUplink = blockUplink;
    }

    public Integer getCounterDown() {
        return counterDown;
    }

    public void setCounterDown(Integer counterDown) {
        this.counterDown = counterDown;
    }

    public Integer getCounterUp() {
        return counterUp;
    }

    public void setCounterUp(Integer counterUp) {
        this.counterUp = counterUp;
    }

    public Integer getCountersSize() {
        return countersSize;
    }

    public void setCountersSize(Integer countersSize) {
        this.countersSize = countersSize;
    }

    public Double getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Double createdAt) {
        this.createdAt = createdAt;
    }

    public String getDevAddr() {
        return devAddr;
    }

    public void setDevAddr(String devAddr) {
        this.devAddr = devAddr;
    }

    public String getDevClass() {
        return devClass;
    }

    public void setDevClass(String devClass) {
        this.devClass = devClass;
    }

    public String getDevEui() {
        return devEui;
    }

    public void setDevEui(String devEui) {
        this.devEui = devEui;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public Double getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Double lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Double getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(Double lastJoin) {
        this.lastJoin = lastJoin;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getLorawanVersion() {
        return lorawanVersion;
    }

    public void setLorawanVersion(String lorawanVersion) {
        this.lorawanVersion = lorawanVersion;
    }

    public Boolean getMulticast() {
        return multicast;
    }

    public void setMulticast(Boolean multicast) {
        this.multicast = multicast;
    }

    public String getNwkskey() {
        return nwkskey;
    }

    public void setNwkskey(String nwkskey) {
        this.nwkskey = nwkskey;
    }

    public Rx1 getRx1() {
        return rx1;
    }

    public void setRx1(Rx1 rx1) {
        this.rx1 = rx1;
    }

    public Rx2 getRx2() {
        return rx2;
    }

    public void setRx2(Rx2 rx2) {
        this.rx2 = rx2;
    }

    public Boolean getStrictCounter() {
        return strictCounter;
    }

    public void setStrictCounter(Boolean strictCounter) {
        this.strictCounter = strictCounter;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Útil para debug no console do Spring
    @Override
    public String toString() {
        return "EverynetDevice [devEui=" + devEui + ", appEui=" + appEui + "]";
    }
}
