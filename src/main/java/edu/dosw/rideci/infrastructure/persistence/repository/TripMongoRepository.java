package edu.dosw.rideci.infrastructure.persistence.repository;

import edu.dosw.rideci.infrastructure.persistence.entity.TripDocument;
import edu.dosw.rideci.domain.model.enums.TripStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio MongoDB para viajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface TripMongoRepository extends MongoRepository<TripDocument, Long> {
    List<TripDocument> findByStatus(TripStatus status);
    long countByStatus(TripStatus status);
    long countByStartTimeBetween(LocalDateTime from, LocalDateTime to);
    List<TripDocument> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);
}
