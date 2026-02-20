package br.rafaeros.aura.modules.findmy.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FindMyLocationReportDTO {
    
    // O ID original do payload da Apple (que mapeia para o hashedPublicKey do FindMyDevice)
    private String id; 
    
    private Double latitude;
    private Double longitude;
    private Integer accuracy;
    private Integer confidence;
    private String batteryStatus;
    
    private LocalDateTime timestamp;
    private LocalDateTime published;
}