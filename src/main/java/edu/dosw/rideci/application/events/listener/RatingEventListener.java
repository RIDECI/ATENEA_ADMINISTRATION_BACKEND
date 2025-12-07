package edu.dosw.rideci.application.events.listener;

import edu.dosw.rideci.application.events.RatingCreatedEvent;
import edu.dosw.rideci.application.service.ReputationService;
import edu.dosw.rideci.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


/**
 * Listener para eventos de calificaciones en RideECI
 * Procesa la creación de nuevas calificaciones y actualiza la reputación de usuarios
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RatingEventListener {

    private final ReputationService reputationService;

    /**
     * Procesa eventos de calificaciones creadas
     * Valida el evento y delega el procesamiento al servicio de reputación
     *
     * @param e Evento de calificación creada
     */
    @RabbitListener(queues = RabbitMQConfig.RATING_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleRatingCreated(RatingCreatedEvent e) {
        if (e == null || e.getRatedProfileId() == null) {
            log.warn("Received null RatingCreatedEvent or missing ratedProfileId, ignoring");
            return;
        }
        try {
            log.info("RatingCreatedEvent received ratedProfileId={} score={}", e.getRatedProfileId(), e.getScore());
            reputationService.handleRatingCreated(e);
            log.info("Rating processed ratedProfileId={}", e.getRatedProfileId());
        } catch (Exception ex) {
            log.error("Error processing RatingCreatedEvent ratedProfileId={} : {}", e.getRatedProfileId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
