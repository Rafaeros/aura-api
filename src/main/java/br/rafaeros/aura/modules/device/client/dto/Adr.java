package br.rafaeros.aura.modules.device.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Adr {

    private Integer datarate;
    private Boolean enabled;

    @JsonProperty("max_tx_power")
    private Integer maxTxPower;

    private String mode;

    @JsonProperty("tx_power")
    private Integer txPower;

    public Adr() {
    }

    public Adr(Integer datarate, Boolean enabled, Integer maxTxPower, String mode, Integer txPower) {
        this.datarate = datarate;
        this.enabled = enabled;
        this.maxTxPower = maxTxPower;
        this.mode = mode;
        this.txPower = txPower;
    }

    public Integer getDatarate() {
        return datarate;
    }

    public void setDatarate(Integer datarate) {
        this.datarate = datarate;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getMaxTxPower() {
        return maxTxPower;
    }

    public void setMaxTxPower(Integer maxTxPower) {
        this.maxTxPower = maxTxPower;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }
}