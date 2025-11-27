package edu.dosw.rideci.application.events;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * Evento de dominio para reporte creado en RideECI
 * Se publica cuando se genera un nuevo reporte de seguridad en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@Builder
public class ReportCreatedEvent {
    private String reportId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private String type;
}
