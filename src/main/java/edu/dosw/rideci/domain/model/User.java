package edu.dosw.rideci.domain.model;

import lombok.*;


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
    private int suspensionCount;
    private boolean blocked;
    private String phoneNumber;
}
