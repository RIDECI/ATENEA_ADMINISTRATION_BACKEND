package edu.dosw.rideci.application.events;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
/**
 * Evento de dominio para carga de documentos de conductor en RideECI
 * Se publica cuando un conductor carga un documento en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
public class DriverDocumentUploadedEvent {
    private Long driverId;
    private String fileId;
    private String type;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
}