package edu.dosw.rideci.domain.model;

import lombok.*;

/**
 * Modelo de dominio para perfiles de usuario en RideECI
 * Representa la información básica de un perfil de usuario en el sistema de administración
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileType;
    private String state;
}
