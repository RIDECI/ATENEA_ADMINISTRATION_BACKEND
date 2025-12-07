package edu.dosw.rideci.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Documento de calificación para persistencia en MongoDB
 * Representa una calificación en la base de datos
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ratings")
public class RatingDocument {

    @Id
    private String id;

    private Long ratingId;
    private Long tripId;
    private Long raterProfileId;
    private Long ratedProfileId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}
