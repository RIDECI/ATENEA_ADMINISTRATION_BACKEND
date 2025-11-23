package edu.eci.ATENEA_Administration_BackEnd.application.events;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
/**
 * Evento de dominio para rechazo de conductor en RideECI
 * Se publica cuando un administrador rechaza a un conductor en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
public class DriverRejectedEvent {
    private Long driverId;
    private Long adminId;
    private String reason;
    private LocalDateTime rejectedAt;
}
