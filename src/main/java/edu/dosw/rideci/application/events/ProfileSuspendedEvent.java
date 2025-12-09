package edu.dosw.rideci.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de dominio para suspensión de perfil en RideECI
 * Se publica cuando un administrador suspende un perfil de usuario
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSuspendedEvent {
    private String suspensionId;
    private Long userId;
    private Long adminId;
    private String profileType;
    private String reason;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
