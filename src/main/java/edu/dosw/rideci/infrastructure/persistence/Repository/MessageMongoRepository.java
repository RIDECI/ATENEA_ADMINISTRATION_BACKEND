package edu.dosw.rideci.infrastructure.persistence.Repository;

import edu.dosw.rideci.infrastructure.persistence.Entity.MessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio MongoDB para mensajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface MessageMongoRepository extends MongoRepository<MessageDocument, String> {
}
