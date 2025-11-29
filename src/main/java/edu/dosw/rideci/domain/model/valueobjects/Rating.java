package edu.dosw.rideci.domain.model.valueobjects;

import lombok.*;
import java.time.LocalDateTime;

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
