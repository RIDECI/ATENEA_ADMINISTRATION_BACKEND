package edu.dosw.rideci.domain.model.valueobjects;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Value Object para mensajes en RideECI
 * Representa un mensaje del sistema de chat con metadatos temporales
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private String messageId;
    private String conversationId;
    private String senderId;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime receivedAt;
}
