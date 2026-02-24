package br.rafaeros.aura.modules.ble.controller.dto;

import java.util.List;

public record HaystackRequestDTO(
    String appleId,
    String password,
    List<KeyDTO> keys
) {
    public record KeyDTO(
        String hashedPublicKey,
        String privateKeyBase64
    ) {}
}