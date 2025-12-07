package edu.dosw.rideci.application.events.listener;

import edu.dosw.rideci.application.events.ReportCreatedEvent;
import edu.dosw.rideci.application.service.ReportService;
import edu.dosw.rideci.domain.model.SecurityReport;
import edu.dosw.rideci.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * Listener para eventos de creación de reportes en RideECI
 * Procesa eventos de reportes creados en otros servicios y los persiste
 * como SecurityReport en el módulo de administración
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReportEventListener {

    private final ReportService reportService;

    /**
     * Procesa un evento de reporte creado recibido por RabbitMQ
     * Convierte el evento a entidad SecurityReport y la persiste en el sistema
     * Incluye validación, transformación de datos y manejo de errores robusto
     *
     * @param event Evento de reporte creado con información completa del incidente
     */
    @RabbitListener(queues = RabbitMQConfig.REPORT_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleReportCreated(ReportCreatedEvent event) {
        try {
            if (event == null || event.getReportId() == null) {
                log.warn("handleReportCreated: evento nulo o reportId nulo");
                return;
            }
            log.info("✅ ReportCreatedEvent recibido: reportId={} type={} userId={}", event.getReportId(),
                    event.getType(), event.getUserId());

            SecurityReport r = SecurityReport.builder()
                    .id(event.getReportId())
                    .title(event.getType() != null ? event.getType().name() + " report" : "report")
                    .type(event.getType() != null ? event.getType().name() : null)
                    .createdBy(event.getUserId())
                    .description(composeDescription(event))
                    .occurredAt(event.getCreatedAt() != null ? event.getCreatedAt() : LocalDateTime.now())
                    .relatedId(determineRelatedId(event))
                    .severity(null)
                    .createdAt(LocalDateTime.now())
                    .status(event.getStatus() != null ? event.getStatus().name() : "PENDING")
                    .build();

            reportService.createReport(r);
            log.info("✅ Report persisted: reportId={}", event.getReportId());
        } catch (Exception ex) {
            log.error("❌ Error procesando ReportCreatedEvent reportId={} : {}",
                    event != null ? event.getReportId() : "null", ex.getMessage(), ex);
            throw ex;
        }
    }


    /**
     * Determina el ID relacionado para el reporte de seguridad
     * Prioriza targetId sobre tripId para establecer la relación
     *
     * @param e Evento de reporte creado
     * @return ID relacionado como String, o null si no hay ninguno
     */
    private static String determineRelatedId(ReportCreatedEvent e) {
        if (e.getTargetId() != null) return String.valueOf(e.getTargetId());
        if (e.getTripId() != null) return String.valueOf(e.getTripId());
        return null;
    }

    /**
     * Compone la descripción del reporte combinando múltiples campos del evento
     * Incluye descripción, evidencia y ubicación en un formato estructurado
     *
     * @param e Evento de reporte creado
     * @return String con descripción completa del incidente
     */
    private static String composeDescription(ReportCreatedEvent e) {
        StringBuilder sb = new StringBuilder();
        if (e.getDescription() != null) sb.append(e.getDescription());
        if (e.getEvidence() != null && !e.getEvidence().isBlank()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("evidence=").append(e.getEvidence());
        }
        if (e.getLocation() != null) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("location=").append(e.getLocation().getLatitude())
                    .append(",").append(e.getLocation().getLongitude());
            if (e.getLocation().getDirection() != null) {
                sb.append(" - ").append(e.getLocation().getDirection());
            }
        }
        return sb.toString();
    }
}
