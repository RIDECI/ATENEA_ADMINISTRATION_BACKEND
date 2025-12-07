package edu.dosw.rideci.infrastructure.controller.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de calificación en RideECI
 * Representa una calificación en formato de respuesta API
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponseDto {
    private String id;
    private Long ratingId;
    private Long tripId;
    private Long raterProfileId;
    private Long ratedProfileId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}