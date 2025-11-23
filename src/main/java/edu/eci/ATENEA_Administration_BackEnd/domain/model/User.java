package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import lombok.*;
import java.time.LocalDateTime;


/**
 * Modelo de dominio para usuario en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String status;
    private double reputation;
    private LocalDateTime lastLogin;
    private String phoneNumber;
    private LocalDateTime createdAt;
}
