package edu.dosw.rideci.application.events;

import edu.dosw.rideci.domain.model.enums.ReportStatus;
import edu.dosw.rideci.domain.model.enums.ReportType;
import edu.dosw.rideci.domain.model.valueobjects.Location;
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
    private Long userId;
    private Long targetId;
    private Long tripId;
    private ReportType type;
    private Location location;
    private String description;
    private LocalDateTime createdAt;
    private ReportStatus status;
    private String evidence;
}