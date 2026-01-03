package br.rafaeros.aura.modules.device.controller;

import br.rafaeros.aura.modules.device.controller.dto.DeviceCreateDTO;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.service.DeviceService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'OWNER')")
    public ResponseEntity<?> create(
            @RequestBody DeviceCreateDTO dto,
            Authentication authentication) {
        Device savedDevice = deviceService.registerDevice(dto, authentication.getName());
        return ResponseEntity.ok(savedDevice);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getAll(Authentication authentication) {
        List<Device> devices = deviceService.listMyDevices(authentication.getName());
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.canManageDevice(authentication, #id)")
    public ResponseEntity<?> getById(@PathVariable Long id, Authentication authentication) {
        try {
            Device device = deviceService.findById(id, authentication.getName());
            return ResponseEntity.ok(device);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.canManageDevice(authentication, #id)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        deviceService.unlinkDevice(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}