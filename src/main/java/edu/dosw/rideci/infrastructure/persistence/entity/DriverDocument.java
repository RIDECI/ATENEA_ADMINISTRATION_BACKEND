package edu.dosw.rideci.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento de conductor para persistencia en MongoDB
 *
 * @author RideECI
 * @version 1.0
 */
@Document(collection = "drivers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDocument {

    @Id
    private Long driverId;

    @Indexed(unique = true)
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
