package edu.dosw.rideci.application.events.command;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Comando para mensaje recibido en RideECI
 * Representa un mensaje de chat recibido que necesita ser procesado
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedMessageCommand {
    private String conversationId;
    private String messageId;
    private String senderId;
    private String content;
    private LocalDateTime sentAt;
}
