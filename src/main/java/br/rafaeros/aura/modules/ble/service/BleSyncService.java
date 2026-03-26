package br.rafaeros.aura.modules.ble.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.rafaeros.aura.modules.ble.controller.dto.HaystackRequestDTO;
import br.rafaeros.aura.modules.ble.model.BleDevice;
import br.rafaeros.aura.modules.ble.repository.BleDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BleSyncService {

    private final BleDeviceRepository deviceRepository;
    private final RestTemplate restTemplate;

    @Value("${app.worker.python.url}")
    private String pythonWorkerUrl;

    @Value("${apple.id}")
    private String appleId;

    @Value("${apple.password}")
    private String applePassword;

    @Scheduled(fixedDelay = 1800000)
    @Async
    public void syncDevices() {
        List<BleDevice> devices = deviceRepository.findAll();
        if (devices.isEmpty()) {
            log.info("Nenhum dispositivo BLE encontrado para sincronizar.");
            return;
        }

        List<HaystackRequestDTO.KeyDTO> keys = devices.stream()
                .map(device -> new HaystackRequestDTO.KeyDTO(device.getHashedPublicKey(), device.getPrivateKeyBase64()))
                .toList();

        HaystackRequestDTO request = new HaystackRequestDTO(appleId, applePassword, keys);

        try {
            log.info("Iniciando sincronização de {} dispositivos Find My...", keys.size());

            String responseJson = restTemplate.postForObject(pythonWorkerUrl, request, String.class);
            
            log.info("Resposta do Python recebida com sucesso. Payload: {}", responseJson);

        } catch (Exception e) {
            log.error("Erro ao chamar o Macless Worker: {}", e.getMessage());
        }
    }
}