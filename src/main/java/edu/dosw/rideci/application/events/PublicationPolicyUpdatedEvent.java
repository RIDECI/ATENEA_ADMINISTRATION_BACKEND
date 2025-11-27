package edu.dosw.rideci.application.events;

import edu.dosw.rideci.domain.model.PublicationPolicy;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Evento de dominio para política de publicación actualizada en RideECI
 * Se publica cuando un administrador actualiza una política de publicación existente
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@Builder
public class PublicationPolicyUpdatedEvent {
    private String policyId;
    private PublicationPolicy policy;
    private Long adminId;
    private LocalDateTime updatedAt;
}
