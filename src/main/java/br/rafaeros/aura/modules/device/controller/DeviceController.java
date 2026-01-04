package br.rafaeros.aura.modules.device.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.rafaeros.aura.modules.device.controller.dto.DeviceCreateDTO;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.service.DeviceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'OWNER')")
    public ResponseEntity<Device> create(
            @RequestBody @Valid DeviceCreateDTO dto, Authentication authentication) {

        Device savedDevice = deviceService.createDevice(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Device>> getAll(Authentication authentication) {
        List<Device> devices = deviceService.listDevicesSmart(authentication.getName());
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Device> getById(@PathVariable Long id, Authentication authentication) {
        Device device = deviceService.findById(id, authentication.getName());
        return ResponseEntity.ok(device);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        deviceService.unlinkDevice(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}