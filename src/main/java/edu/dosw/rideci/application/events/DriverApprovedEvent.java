package edu.dosw.rideci.application.events;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
/**
 * Evento de dominio para aprobación de conductor en RideECI
 * Se publica cuando un administrador aprueba a un conductor en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
public class DriverApprovedEvent {
    private Long driverId;
    private Long adminId;
    private LocalDateTime verifiedAt;
}