package edu.dosw.rideci.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
