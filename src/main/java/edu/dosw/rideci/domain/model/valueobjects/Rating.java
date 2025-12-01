package edu.dosw.rideci.domain.model.valueobjects;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Value Object para calificaciones en RideECI
 * Representa una calificación entre usuarios después de un viaje
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    private Long id;
    private Long raterProfileId;
    private Long ratedProfileId;
    private Long tripId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}
