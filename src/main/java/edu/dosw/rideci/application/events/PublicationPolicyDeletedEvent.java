package edu.dosw.rideci.application.events;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Evento de dominio para política de publicación eliminada en RideECI
 * Se publica cuando un administrador elimina una política de publicación
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@Builder
public class PublicationPolicyDeletedEvent {
    private String policyId;
    private String policyName;
    private Long adminId;
    private LocalDateTime deletedAt;
}
