package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.UserDocument;

/**
 * Repositorio MongoDB para usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface UserMongoRepository extends MongoRepository<UserDocument, Long> {
    List<UserDocument> findByNameContainingIgnoreCase(String q);
    List<UserDocument> findByState(String state);
}
