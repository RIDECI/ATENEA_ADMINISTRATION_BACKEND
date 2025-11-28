package edu.dosw.rideci.infrastructure.adapters.messaging;

import edu.dosw.rideci.application.port.out.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador RabbitMQ para publicación de eventos en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@RequiredArgsConstructor
@Component("rabbitPublisherAdapter")
public class RabbitPublisherAdapter implements EventPublisher {
    private final RabbitTemplate rabbitTemplate;

    /**
     * Publica un evento en RabbitMQ
     *
     * @param event Evento a publicar
     * @param routingKey Clave de enrutamiento
     */
    @Override
    public void publish(Object event, String routingKey) {
        rabbitTemplate.convertAndSend("admin.exchange", routingKey, event);
    }

    @Override
    public void publish(Object event, String exchange, String routingKey) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
