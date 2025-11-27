package edu.dosw.rideci.infrastructure.persistence.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Documento de mensaje para persistencia en MongoDB
 *
 * @author RideECI
 * @version 1.0
 */
@Document(collection = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDocument {
    @Id
    private String messageId;
    private String conversationId;
    private String senderId;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime receivedAt;
}
