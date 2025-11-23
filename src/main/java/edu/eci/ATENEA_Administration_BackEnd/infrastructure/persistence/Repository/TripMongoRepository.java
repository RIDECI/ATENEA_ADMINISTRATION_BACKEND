package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository;

import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.TripDocument;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.Enum.TripStatus;
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
