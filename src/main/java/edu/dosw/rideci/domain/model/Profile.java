package edu.dosw.rideci.domain.model;

import edu.dosw.rideci.domain.model.enums.IdentificationType;
import edu.dosw.rideci.domain.model.valueobjects.Badge;
import edu.dosw.rideci.domain.model.valueobjects.Reputation;
import edu.dosw.rideci.domain.model.valueobjects.Vehicle;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<Vehicle> vehicles;
    private Reputation calification;
    private List<Badge> badges;
    private String identificationNumber;
    private String address;
    private LocalDateTime birthDate;
    private String profilePictureUrl;
    private IdentificationType identificationType;
    private LocalDateTime updatedAt;
}
