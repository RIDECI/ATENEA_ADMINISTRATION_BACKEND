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
 * Evento de dominio para validación aprobada en RideECI
 * Se publica cuando un validador aprueba una solicitud en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
public class ValidationApprovedEvent {
    private String requestId;
    private Long userId;
    private Long validatorId;
    private LocalDateTime approvedAt;
}
