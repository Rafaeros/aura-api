package br.rafaeros.aura.modules.jiga.controller.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JigaProvisioningDTO {
    @NotBlank(message = "O ID único do dispositivo (deviceId) é obrigatório")
    private String deviceId;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String productName;

    @NotBlank(message = "A versão do produto é obrigatória")
    private String productVersion;

    private Map<String, Object> dynamicAttributes = new HashMap<>();

    @JsonAnySetter
    public void addDynamicAttribute(String key, Object value) {
        this.dynamicAttributes.put(key, value);
    }
}
