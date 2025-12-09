package edu.dosw.rideci.application.events;

import edu.dosw.rideci.domain.model.enums.IdentificationType;
import edu.dosw.rideci.domain.model.enums.ProfileType;
import edu.dosw.rideci.domain.model.valueobjects.Badge;
import edu.dosw.rideci.domain.model.valueobjects.Reputation;
import edu.dosw.rideci.domain.model.valueobjects.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Evento de comando para el modulo de perfiles en RideECI
 * Se utiliza para activar o desactivar perfiles de usuario en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileEvent {
    private Long userId;
    private String name;
    private List<Vehicle> vehicles;
    private Reputation calification;
    private String phoneNumber;
    private ProfileType profileType;
    private List<Long> ratings;
    private List<Badge> badges;
    private String email;
    private String identificationNumber;
    private String address;
    private LocalDateTime birthDate;
    private String profilePictureUrl;
    private IdentificationType identificationType;
    private String state; // ACTIVE / INACTIVE / SUSPENDED
    private LocalDateTime updatedAt;
}
