package edu.dosw.rideci.infrastructure.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta de conductor en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private Long driverId;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleColor;
    private String plate;
    private String licenseNumber;
    private LocalDateTime licenseExpiry;
    private List<String> documentRefs;
    private String status;
    private String rejectionReason;
    private LocalDateTime verifiedAt;
}
