package edu.dosw.rideci.infrastructure.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import edu.dosw.rideci.infrastructure.persistence.entity.RatingDocument;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio MongoDB para calificaciones en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface RatingMongoRepository extends MongoRepository<RatingDocument, String> {
    Optional<RatingDocument> findByRatingId(Long ratingId);
    List<RatingDocument> findAllByRatedProfileId(Long ratedProfileId);
}
