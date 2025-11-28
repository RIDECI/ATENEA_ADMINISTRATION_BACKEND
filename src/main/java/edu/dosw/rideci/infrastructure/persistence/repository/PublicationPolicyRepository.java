package edu.dosw.rideci.infrastructure.persistence.repository;

import edu.dosw.rideci.infrastructure.persistence.entity.PublicationPolicyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para políticas de publicación en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface PublicationPolicyRepository extends MongoRepository<PublicationPolicyDocument, String> {
}
