package edu.dosw.rideci.application.events.listener;

import edu.dosw.rideci.application.events.MessageSentEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import edu.dosw.rideci.infrastructure.configs.RabbitMQConfig;
import edu.dosw.rideci.application.service.MessageEventService;

/**
 * Listener para eventos de mensajes en RideECI
 * Procesa mensajes enviados en el sistema de chat
 *
 * @author RideECI
 * @version 1.0
 */
@Component
public class MessageEventListener {

    private final MessageEventService messageEventService;

    public MessageEventListener(MessageEventService messageEventService) {
        this.messageEventService = messageEventService;
    }

    /**
     * Procesa eventos de mensajes enviados desde la cola de chat
     *
     * @param event Evento de mensaje enviado
     */
    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void handleMessageSent(MessageSentEvent event) {
        try {
            System.out.println("📨 MessageSentEvent recibido");
            messageEventService.processMessageSent(event);
            System.out.println("✅ MessageSentEvent procesado");
        } catch (Exception ex) {
            System.err.println("❌ Error procesando MessageSentEvent: " + ex.getMessage());
            throw ex;
        }
    }
}
