package edu.dosw.rideci.infrastructure.adapters.messaging;

import edu.dosw.rideci.application.port.out.EventPublisher;
import edu.dosw.rideci.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.rabbit.default-exchange:admin.exchange}")
    private String defaultExchange;

    @Override
    public void publish(Object event, String routingKey) {
        if (routingKey == null) routingKey = "";
        String exchange;
        if (routingKey.startsWith("travel.")) {
            exchange = RabbitMQConfig.TRIP_EXCHANGE;
        } else if (routingKey.startsWith("profile.")) {
            exchange = RabbitMQConfig.EXCHANGE_PROFILE;
        } else if (routingKey.startsWith("user.")) {
            exchange = RabbitMQConfig.EXCHANGE_USER;
        } else if (routingKey.startsWith("admin.")) {
            exchange = RabbitMQConfig.EXCHANGE;
        } else {
            exchange = defaultExchange;
        }
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

    @Override
    public void publish(Object event, String exchange, String routingKey) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
