package edu.dosw.rideci.application.events;

import edu.dosw.rideci.domain.model.enums.IdentificationType;
import edu.dosw.rideci.domain.model.enums.ProfileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Evento de comando para el modulo de perfiles en RideECI
 * Se utiliza para activar o desactivar perfiles de usuario en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEvent {
    private Long userId;
    private String name;
    private String email;
    private IdentificationType identificationType;
    private String identificationNumber;
    private String phoneNumber;
    private String address;
    private String role;
    private ProfileType profileType;
    private String state;
}
