package edu.dosw.rideci.application.events.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.dosw.rideci.application.events.ReportCreatedEvent;
import edu.dosw.rideci.application.service.ReportService;
import edu.dosw.rideci.domain.model.SecurityReport;
import edu.dosw.rideci.domain.model.enums.ManualReason;
import edu.dosw.rideci.domain.model.enums.ReportType;
import edu.dosw.rideci.domain.model.valueobjects.Location;
import edu.dosw.rideci.domain.model.valueobjects.ManualReportRequest;
import edu.dosw.rideci.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Listener para eventos de creación de reportes en RideECI
 * Procesa eventos de reportes creados y los convierte en SecurityReport para persistencia
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReportEventListener {

    private final ReportService reportService;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
     * Procesa eventos de reportes creados desde la cola RabbitMQ
     * Convierte ReportCreatedEvent a SecurityReport y lo persiste
     *
     * @param event Evento de reporte creado
     */
    @RabbitListener(queues = RabbitMQConfig.REPORT_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleReportCreated(ReportCreatedEvent event) {
        if (event == null || event.getReportId() == null) {
            log.warn("handleReportCreated: evento nulo o reportId nulo");
            return;
        }

        log.info("✅ ReportCreatedEvent recibido: reportId={} type={} userId={}",
                event.getReportId(), event.getType(), event.getUserId());

        try {
            SecurityReport r;
            if (event.getType() == ReportType.MANUAL) {
                r = createForManual(event);
            } else {
                r = createForDefault(event);
            }

            reportService.createReport(r);
            log.info("✅ Report persisted: reportId={}", event.getReportId());
        } catch (Exception ex) {

            String id = event.getReportId() != null ? event.getReportId() : "null";
            log.error("❌ Error procesando ReportCreatedEvent reportId={} : {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }



    /**
     * Crea un SecurityReport para eventos de tipo MANUAL
     * Incluye procesamiento especial para reportes manuales con detalles adicionales
     *
     * @param event Evento de reporte creado
     * @return SecurityReport para reportes manuales
     */
    private SecurityReport createForManual(ReportCreatedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (event.getReportId() == null) {
            throw new IllegalArgumentException("event.reportId must not be null");
        }

        ManualReportRequest manual = tryParseManualRequest(event);
        String title = buildManualTitle(event, manual);
        String description = composeDescription(manual, event);
        String severity = mapSeverity(getManualReason(manual));

        return SecurityReport.builder()
                .id(event.getReportId())
                .title(title)
                .type(event.getType() != null ? event.getType().name() : null)
                .createdBy(event.getUserId())
                .description(description)
                .occurredAt(nonNullOrNow(event.getCreatedAt()))
                .relatedId(determineRelatedId(event, manual))
                .severity(severity)
                .createdAt(LocalDateTime.now())
                .status(event.getStatus() != null ? event.getStatus().name() : "PENDING")
                .build();
    }


    /**
     * Crea un SecurityReport para eventos no manuales (por defecto)
     *
     * @param event Evento de reporte creado
     * @return SecurityReport para reportes no manuales
     */
    private SecurityReport createForDefault(ReportCreatedEvent event) {
        return SecurityReport.builder()
                .id(event.getReportId())
                .title(event.getType() != null ? event.getType().name() + " report" : "report")
                .type(event.getType() != null ? event.getType().name() : null)
                .createdBy(event.getUserId())
                .description(composeDescription(event))
                .occurredAt(nonNullOrNow(event.getCreatedAt()))
                .relatedId(determineRelatedId(event))
                .severity(null)
                .createdAt(LocalDateTime.now())
                .status(event.getStatus() != null ? event.getStatus().name() : "PENDING")
                .build();
    }

    /**
     * Obtiene la fecha proporcionada o la fecha/hora actual si es nula
     *
     * @param t Fecha/hora a verificar
     * @return Fecha/hora no nula
     */
    private static LocalDateTime nonNullOrNow(LocalDateTime t) {
        return t != null ? t : LocalDateTime.now();
    }

    /**
     * Intenta deserializar event.description a ManualReportRequest si es JSON.
     * Si no es posible, devuelve un ManualReportRequest construido a partir
     * de los campos sueltos del evento.
     */
    private static ManualReportRequest tryParseManualRequest(ReportCreatedEvent e) {
        if (e == null) return null;

        String desc = e.getDescription();
        if (desc != null && desc.trim().startsWith("{")) {
            try {
                return MAPPER.readValue(desc, ManualReportRequest.class);
            } catch (JsonProcessingException ex) {
                log.warn("tryParseManualRequest: no se pudo deserializar description a ManualReportRequest: {}", ex.getMessage());
            }
        }

        return ManualReportRequest.builder()
                .userId(e.getUserId())
                .tripId(e.getTripId())
                .targetId(e.getTargetId())
                .description(e.getDescription())
                .evidence(e.getEvidence())
                .location(e.getLocation())
                .build();
    }


    /**
     * Obtiene el motivo manual del reporte
     *
     * @param m ManualReportRequest
     * @return ManualReason o null
     */
    private static ManualReason getManualReason(ManualReportRequest m) {
        return m != null ? m.getReason() : null;
    }


    /**
     * Obtiene evidencia del reporte manual o evento
     *
     * @param m ManualReportRequest
     * @param e ReportCreatedEvent
     * @return Evidencia o null
     */
    private static String getEvidence(ManualReportRequest m, ReportCreatedEvent e) {
        if (m != null && m.getEvidence() != null && !m.getEvidence().isBlank()) return m.getEvidence();
        if (e != null && e.getEvidence() != null && !e.getEvidence().isBlank()) return e.getEvidence();
        return null;
    }

    /**
     * Obtiene ubicación del reporte manual o evento
     *
     * @param m ManualReportRequest
     * @param e ReportCreatedEvent
     * @return Location o null
     */
    private static Location getLocationFrom(ManualReportRequest m, ReportCreatedEvent e) {
        if (m != null && m.getLocation() != null) return m.getLocation();
        if (e != null && e.getLocation() != null) return e.getLocation();
        return null;
    }


    /**
     * Determina el ID relacionado para reportes manuales
     *
     * @param e Evento de reporte
     * @param manual Request manual
     * @return ID relacionado como string
     */
    private static String determineRelatedId(ReportCreatedEvent e, ManualReportRequest manual) {
        if (manual != null && manual.getTargetId() != null) return String.valueOf(manual.getTargetId());
        if (manual != null && manual.getTripId() != null) return String.valueOf(manual.getTripId());
        return determineRelatedId(e);
    }

    /**
     * Determina el ID relacionado para reportes no manuales
     *
     * @param e Evento de reporte
     * @return ID relacionado como string
     */
    private static String determineRelatedId(ReportCreatedEvent e) {
        if (e == null) return null;
        if (e.getTargetId() != null) return String.valueOf(e.getTargetId());
        if (e.getTripId() != null) return String.valueOf(e.getTripId());
        return null;
    }


    /**
     * Compone descripción para reportes manuales
     *
     * @param m ManualReportRequest
     * @param e ReportCreatedEvent
     * @return Descripción formateada
     */
    private static String composeDescription(ManualReportRequest m, ReportCreatedEvent e) {
        StringBuilder sb = new StringBuilder();

        String mainDesc = null;
        if (m != null && m.getDescription() != null && !m.getDescription().isBlank()) {
            mainDesc = m.getDescription();
        } else if (e != null && e.getDescription() != null && !e.getDescription().isBlank()) {
            mainDesc = e.getDescription();
        }
        appendIfPresent(sb, mainDesc);

        ManualReason reason = getManualReason(m);
        if (reason != null) {
            appendIfPresent(sb, "reason=" + reason.name());
        }

        String evidence = getEvidence(m, e);
        if (evidence != null && !evidence.isBlank()) {
            appendIfPresent(sb, "evidence=" + evidence);
        }

        Location loc = getLocationFrom(m, e);
        appendLocation(sb, loc);

        return sb.toString();
    }



    /**
     * Compone descripción para reportes no manuales
     *
     * @param e ReportCreatedEvent
     * @return Descripción formateada
     */
    private static String composeDescription(ReportCreatedEvent e) {
        StringBuilder sb = new StringBuilder();
        appendIfPresent(sb, e != null && e.getDescription() != null ? e.getDescription() : null);
        appendIfPresent(sb, e != null && e.getEvidence() != null && !e.getEvidence().isBlank() ? "evidence=" + e.getEvidence() : null);
        appendLocation(sb, e != null ? e.getLocation() : null);
        return sb.toString();
    }

    /**
     * Agrega fragmento al StringBuilder si no es nulo ni vacío
     *
     * @param sb StringBuilder
     * @param fragment Fragmento a agregar
     */
    private static void appendIfPresent(StringBuilder sb, String fragment) {
        if (fragment == null || fragment.isBlank()) return;
        if (!sb.isEmpty()) sb.append(" | ");
        sb.append(fragment);
    }

    /**
     * Agrega información de ubicación al StringBuilder
     *
     * @param sb StringBuilder
     * @param loc Ubicación
     */
    private static void appendLocation(StringBuilder sb, Location loc) {
        if (loc == null) return;
        StringBuilder locSb = new StringBuilder();
        locSb.append("location=").append(loc.getLatitude()).append(",").append(loc.getLongitude());
        if (loc.getDirection() != null && !loc.getDirection().isBlank()) {
            locSb.append(" - ").append(loc.getDirection());
        }
        appendIfPresent(sb, locSb.toString());
    }


    /**
     * Construye título para reportes manuales
     *
     * @param event Evento de reporte
     * @param manual Request manual
     * @return Título del reporte
     */
    private static String buildManualTitle(ReportCreatedEvent event, ManualReportRequest manual) {
        if (manual != null && manual.getReason() != null) {
            return manual.getReason().name() + " - MANUAL report";
        }
        if (event != null && event.getType() != null) {
            return event.getType().name() + " report";
        }
        return "MANUAL report";
    }

    /**
     * Mapea la severidad basada en el motivo manual
     *
     * @param reason Motivo manual
     * @return Severidad (HIGH, MEDIUM, o null)
     */
    private static String mapSeverity(ManualReason reason) {
        if (reason == null) return null;
        return switch (reason) {
            case PHYSICAL_AGGRESSION, STEALING -> "HIGH";
            case VERBAL_AGGRESSION, HARASSMENT -> "MEDIUM";
            default -> null;
        };
    }
}
