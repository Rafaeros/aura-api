package br.rafaeros.aura.modules.telemetry.controller.dto;

import java.util.Map;

public class MqttLogDTO {

    private String type;
    private Map<String, String> params;
    private Map<String, String> meta;

    public MqttLogDTO() {
    }

    public MqttLogDTO(String type, Map<String, String> params, Map<String, String> meta) {
        this.type = type;
        this.params = params;
        this.meta = meta;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

}
