package br.rafaeros.aura.modules.device.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Geolocation {
    private Double lat;
    private Double lng;
    private String method;
    private Integer precision;
    private Integer quality;
}
