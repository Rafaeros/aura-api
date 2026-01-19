package br.rafaeros.aura.modules.device.controller.dto;

import java.util.List;
import br.rafaeros.aura.modules.device.model.Device;
import br.rafaeros.aura.modules.device.model.DeviceTag;
import br.rafaeros.aura.modules.device.model.UserDevice;

public record DeviceListResponseDTO (
    Long id,
    String name,
    String devEui,
    List<DeviceTag> tags
) {

    public static DeviceListResponseDTO fromUserDevice(UserDevice userDevice) {
        String displayName = (userDevice.getCustomName() != null && !userDevice.getCustomName().isBlank())
                ? userDevice.getCustomName() 
                : "Device " + userDevice.getDevice().getDevEui();

        return new DeviceListResponseDTO(
            userDevice.getDevice().getId(),
            displayName,
            userDevice.getDevice().getDevEui(),
            userDevice.getDevice().getTags()
        );
    }

    public static DeviceListResponseDTO fromDevice(Device device) {
        String genericName = "Device " + device.getDevEui(); 

        return new DeviceListResponseDTO(
            device.getId(),
            genericName,
            device.getDevEui(),
            device.getTags()
        );
    }
}