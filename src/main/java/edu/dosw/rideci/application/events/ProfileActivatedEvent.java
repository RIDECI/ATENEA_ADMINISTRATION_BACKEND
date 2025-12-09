package edu.dosw.rideci.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de dominio para activación de perfil en RideECI
 * Se publica cuando un administrador activa un perfil de usuario
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileActivatedEvent {
    private Long userId;
    private Long adminId;
    private String profileType;
    private LocalDateTime activatedAt;
}
