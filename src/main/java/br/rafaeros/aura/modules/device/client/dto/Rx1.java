package br.rafaeros.aura.modules.device.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rx1 {

    @JsonProperty("current_delay")
    private Integer currentDelay;

    private Integer delay;
    private String status;

    public Rx1() {}

    public Integer getCurrentDelay() {
        return currentDelay;
    }

    public void setCurrentDelay(Integer currentDelay) {
        this.currentDelay = currentDelay;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
