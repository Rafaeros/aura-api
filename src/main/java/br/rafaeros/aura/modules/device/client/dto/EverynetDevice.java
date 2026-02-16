package br.rafaeros.aura.modules.device.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    // Debug
    @Override
    public String toString() {
        return "EverynetDevice [devEui=" + devEui + ", appEui=" + appEui + "]";
    }
}
