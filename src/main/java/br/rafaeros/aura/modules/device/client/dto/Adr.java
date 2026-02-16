package br.rafaeros.aura.modules.device.client.dto;

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
public class Adr {

    private Integer datarate;
    private Boolean enabled;

    @JsonProperty("max_tx_power")
    private Integer maxTxPower;

    private String mode;

    @JsonProperty("tx_power")
    private Integer txPower;
}
