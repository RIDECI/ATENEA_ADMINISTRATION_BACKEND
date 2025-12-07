package edu.dosw.rideci.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Evento de dominio para calificación creada en RideECI
 * Se publica cuando un usuario califica a otro después de un viaje
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingCreatedEvent {
    private Long ratingId;
    private Long raterProfileId;
    private Long ratedProfileId;
    private Long tripId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
    private String eventType;
}
