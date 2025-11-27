package edu.dosw.rideci.application.events;

import edu.dosw.rideci.domain.model.PublicationPolicy;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Evento de dominio para política de publicación creada en RideECI
 * Se publica cuando un administrador crea una nueva política de publicación
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@Builder
public class PublicationPolicyCreatedEvent {
    private String policyId;
    private PublicationPolicy policy;
    private Long adminId;
    private LocalDateTime createdAt;
}
