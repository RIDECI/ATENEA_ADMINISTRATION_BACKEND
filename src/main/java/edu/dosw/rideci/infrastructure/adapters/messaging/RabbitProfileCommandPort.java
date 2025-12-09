package edu.dosw.rideci.infrastructure.adapters.messaging;

import edu.dosw.rideci.application.port.out.ProfileCommandPort;
import edu.dosw.rideci.application.port.out.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * Adaptador RabbitMQ para comandos de perfil en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class RabbitProfileCommandPort implements ProfileCommandPort {

    private final EventPublisher eventPublisher;

    /**
     * Publica un evento de perfil activado en RabbitMQ
     *
     * @param ev Evento a publicar
     * @param routingKey Clave de enrutamiento
     */
    @Override
    public void publishProfileActivated(Object ev, String routingKey) {
        eventPublisher.publish(ev, routingKey == null ? "profile.activated" : routingKey);
    }

    /**
     * Publica un evento de perfil suspendido en RabbitMQ
     *
     * @param ev Evento a publicar
     * @param routingKey Clave de enrutamiento
     */
    @Override
    public void publishProfileSuspended(Object ev, String routingKey) {
        eventPublisher.publish(ev, routingKey == null ? "profile.suspended" : routingKey);
    }
}
