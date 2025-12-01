package edu.dosw.rideci.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento de dominio para usuario en RideECI
 * Contiene información completa del usuario para sincronización entre servicios
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private Long userId;
    private String name;
    private String email;
    private String recoveryEmail;
    private String identificationType;
    private String identificationNumber;
    private String phoneNumber;
    private String address;
    private String role;
    private String state;
}
