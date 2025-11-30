package edu.dosw.rideci.application.port.out;

import edu.dosw.rideci.domain.model.valueobjects.Rating;

import java.util.List;

public interface ReputationRepositoryPort {
    boolean existsById(Long ratingId);
    Rating saveRating(Rating r);
    double averageForProfile(Long profileId);
    long countForProfile(Long profileId);
    List<Rating> findByRatedProfileId(Long profileId);
}

