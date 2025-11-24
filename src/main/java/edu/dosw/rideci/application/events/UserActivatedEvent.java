package edu.dosw.rideci.application.events;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
/**
 * Evento de dominio para activación de usuario en RideECI
 * Se publica cuando un administrador activa a un usuario en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
public class UserActivatedEvent {
    private Long userId;
    private Long adminId;
    private LocalDateTime activatedAt;
}
