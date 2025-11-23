package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelo de dominio para conductor en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
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
