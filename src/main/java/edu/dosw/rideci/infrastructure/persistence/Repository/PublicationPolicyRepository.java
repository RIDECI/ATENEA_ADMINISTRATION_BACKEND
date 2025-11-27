package edu.dosw.rideci.infrastructure.persistence.Repository;

import edu.dosw.rideci.infrastructure.persistence.Entity.PublicationPolicyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para políticas de publicación en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface PublicationPolicyRepository extends MongoRepository<PublicationPolicyDocument, String> {
}
