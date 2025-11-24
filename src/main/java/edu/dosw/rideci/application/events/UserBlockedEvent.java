package edu.dosw.rideci.application.events;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
/**
 * Evento de dominio para bloqueo de usuario en RideECI
 * Se publica cuando un administrador bloquea a un usuario en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
public class UserBlockedEvent {
    private Long userId;
    private Long adminId;
    private String reason;
    private LocalDateTime blockedAt;
}
