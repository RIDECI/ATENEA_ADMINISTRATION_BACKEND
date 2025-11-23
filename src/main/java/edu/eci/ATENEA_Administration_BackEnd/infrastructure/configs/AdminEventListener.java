package edu.eci.ATENEA_Administration_BackEnd.infrastructure.configs;

import edu.eci.ATENEA_Administration_BackEnd.application.events.*;
import edu.eci.ATENEA_Administration_BackEnd.application.port.out.EventPublisher;
import edu.eci.ATENEA_Administration_BackEnd.application.port.out.SecurityReportRepositoryPort;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.SecurityReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Listener de eventos para administración en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class AdminEventListener {

    private final SecurityReportRepositoryPort reportRepo;
    private final EventPublisher eventPublisher;

    /**
     * Procesa evento de usuario suspendido
     *
     * @param ev Evento de usuario suspendido
     */
    @RabbitListener(queues = RabbitConfig.SUSPEND_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onUserSuspended(UserSuspendedEvent ev) {
        log.info("[AdminEventListener] Received UserSuspendedEvent: {}", ev);

        SecurityReport r = SecurityReport.builder()
                .id(UUID.randomUUID().toString())
                .title("Usuario suspendido: " + ev.getUserId())
                .type("USER_SUSPENSION")
                .createdBy(ev.getAdminId())
                .description(ev.getReason())
                .occurredAt(ev.getStartDate() != null ? ev.getStartDate() : LocalDateTime.now())
                .relatedId(ev.getSuspensionId())
                .build();

        if (!reportRepo.existsByRelatedIdAndType(r.getRelatedId(), r.getType())) {
            reportRepo.save(r);
            eventPublisher.publish(r, "notifications.report.created");
        } else {
            log.info("Reporte ya existente para suspensionId={}, omitiendo", r.getRelatedId());
        }
    }

    /**
     * Procesa evento de validación aprobada
     *
     * @param ev Evento de validación aprobada
     */
    @RabbitListener(queues = RabbitConfig.VALIDATION_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onValidationApproved(ValidationApprovedEvent ev) {
        log.info("[AdminEventListener] Received ValidationApprovedEvent: {}", ev);

        SecurityReport r = SecurityReport.builder()
                .id(UUID.randomUUID().toString())
                .title("Validación aprobada: " + ev.getRequestId())
                .type("VALIDATION_APPROVAL")
                .createdBy(ev.getValidatorId() != null ? ev.getValidatorId() : null)
                .description("Validación aprobada para userId=" + ev.getUserId())
                .occurredAt(ev.getApprovedAt() != null ? ev.getApprovedAt() : LocalDateTime.now())
                .relatedId(ev.getRequestId())
                .build();

        if (!reportRepo.existsByRelatedIdAndType(r.getRelatedId(), r.getType())) {
            reportRepo.save(r);
            eventPublisher.publish(r, "notifications.report.created");
        } else {
            log.info("Reporte de validación ya existe para requestId={}, omitiendo", r.getRelatedId());
        }
    }

    /**
     * Procesa evento de documento de conductor subido
     *
     * @param ev Evento de documento subido
     */
    @RabbitListener(queues = RabbitConfig.DRIVER_DOCUMENT_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onDriverDocumentUploaded(DriverDocumentUploadedEvent ev) {
        log.info("[AdminEventListener] Received DriverDocumentUploadedEvent: {}", ev);

        SecurityReport r = SecurityReport.builder()
                .id(UUID.randomUUID().toString())
                .title("Documento conductor subido: " + ev.getDriverId())
                .type("DOCUMENT_UPLOAD")
                .createdBy(ev.getUploadedBy())
                .description(ev.getType() + ":" + ev.getFileId())
                .occurredAt(ev.getUploadedAt() != null ? ev.getUploadedAt() : LocalDateTime.now())
                .relatedId(ev.getFileId())
                .build();

        if (!reportRepo.existsByRelatedIdAndType(r.getRelatedId(), r.getType())) {
            reportRepo.save(r);
            eventPublisher.publish(r, "notifications.report.created");
        } else {
            log.info("Reporte de documento ya existe para fileId={}, omitiendo", r.getRelatedId());
        }
    }
}
