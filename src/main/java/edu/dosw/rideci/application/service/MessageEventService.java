package edu.dosw.rideci.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.dosw.rideci.application.events.MessageSentEvent;
import edu.dosw.rideci.application.events.command.ReceivedMessageCommand;
import edu.dosw.rideci.application.port.out.MessageRepositoryPort;
import edu.dosw.rideci.domain.model.valueobjects.Message;

import java.time.LocalDateTime;

/**
 * Servicio para procesamiento de eventos de mensajes en RideECI
 * Maneja la recepción y almacenamiento de mensajes del sistema de chat
 *
 * @author RideECI
 * @version 1.0
 */
@Service
public class MessageEventService {

    private final MessageRepositoryPort messageRepo;

    public MessageEventService(MessageRepositoryPort messageRepo) {
        this.messageRepo = messageRepo;
    }

    /**
     * Procesa un evento de mensaje enviado
     * Valida el evento y almacena el mensaje si no existe previamente
     *
     * @param e Evento de mensaje enviado
     */
    @Transactional
    public void processMessageSent(MessageSentEvent e) {
        if (e == null || e.getMessageId() == null) {
            System.err.println("MessageEventService: evento inválido");
            return;
        }

        if (messageRepo.existsById(e.getMessageId())) {
            System.out.println("Mensaje ya procesado (id): " + e.getMessageId());
            return;
        }

        ReceivedMessageCommand cmd = ReceivedMessageCommand.builder()
                .conversationId(e.getConversationId())
                .messageId(e.getMessageId())
                .senderId(e.getSenderId())
                .content(e.getContent())
                .sentAt(e.getSentAt())
                .build();

        Message m = toMessageEntity(cmd);
        messageRepo.save(m);
    }

    /**
     * Convierte un comando de mensaje recibido a entidad de dominio Message
     *
     * @param cmd Comando de mensaje recibido
     * @return Entidad de dominio Message
     */
    private Message toMessageEntity(ReceivedMessageCommand cmd) {
        return Message.builder()
                .messageId(cmd.getMessageId())
                .conversationId(cmd.getConversationId())
                .senderId(cmd.getSenderId())
                .content(cmd.getContent())
                .sentAt(cmd.getSentAt())
                .receivedAt(LocalDateTime.now())
                .build();
    }
}
