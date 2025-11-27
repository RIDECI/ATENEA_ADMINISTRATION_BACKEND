package edu.dosw.rideci.infrastructure.persistence.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import edu.dosw.rideci.infrastructure.persistence.Entity.DriverDocument;

/**
 * Repositorio MongoDB para conductores en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface DriverMongoRepository extends MongoRepository<DriverDocument, Long> {
    List<DriverDocument> findByStatus(String status);
    List<DriverDocument> findByNameContainingIgnoreCase(String q);
}
