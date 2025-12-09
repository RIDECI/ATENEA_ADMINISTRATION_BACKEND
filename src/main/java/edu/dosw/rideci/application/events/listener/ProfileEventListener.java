package edu.dosw.rideci.application.events.listener;

import edu.dosw.rideci.application.events.ProfileEvent;
import edu.dosw.rideci.application.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * Listener para eventos de perfiles en RideECI
 * Procesa la creación de perfiles y sincroniza con la colección de conductores
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class ProfileEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(ProfileEventListener.class);
    private final ProfileService profileService;

    /**
     * Procesa eventos de creación de perfiles
     * Realiza upsert del perfil y sincroniza la colección de conductores si corresponde
     *
     * @param ev del evento
     */
    @RabbitListener(queues = "#{@profileSyncQueue.name}", containerFactory = "rabbitListenerContainerFactory")
    public void onProfileEvent(@Payload ProfileEvent ev) {
        if (ev == null || ev.getUserId() == null) {
            LOG.warn("Ignored invalid ProfileEvent: null or missing userId");
            return;
        }
        try {
            LOG.debug("Received ProfileEvent for userId={}: {}", ev.getUserId(), ev);
            profileService.upsertFromEvent(ev);
            LOG.debug("Upserted profile for userId={}", ev.getUserId());
        } catch (Exception ex) {
            LOG.error("Failed to process ProfileEvent for userId={}", ev.getUserId(), ex);
        }
    }
}