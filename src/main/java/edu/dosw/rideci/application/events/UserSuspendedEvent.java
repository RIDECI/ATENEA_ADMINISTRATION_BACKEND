package edu.dosw.rideci.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Evento de dominio para suspensión de usuario en RideECI
 * Se publica cuando un administrador suspende a un usuario del sistema
 *
 * @author RideECI
 * @version 1.0
 */
public class UserSuspendedEvent {
    private String suspensionId;
    private Long userId;
    private Long adminId;
    private String reason;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
