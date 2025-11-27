package edu.dosw.rideci.application.events;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento de dominio para mensaje enviado en RideECI
 * Se publica cuando un usuario envía un mensaje en el chat
 *
 * @author RideECI
 * @version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageSentEvent {
    private String conversationId;
    private String messageId;
    private String senderId;
    private String content;
    private LocalDateTime sentAt;
}
