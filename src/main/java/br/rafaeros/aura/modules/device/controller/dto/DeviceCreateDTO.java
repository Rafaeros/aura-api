package br.rafaeros.aura.modules.device.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeviceCreateDTO(
        @JsonProperty("name") String name,
        @JsonProperty("dev_eui")
                @Size(min = 16, max = 16, message = "dev_eui must be exactly 16 characters long")
                @NotBlank(message = "Dev EUI cannot be blank")
                String devEui) {}
