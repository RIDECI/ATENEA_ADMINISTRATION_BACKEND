package edu.dosw.rideci.application.events.listener;

import edu.dosw.rideci.application.events.RatingCreatedEvent;
import edu.dosw.rideci.application.service.ReputationService;
import edu.dosw.rideci.infrastructure.configs.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingEventListener {

    private final ReputationService reputationService;

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
