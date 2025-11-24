package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository;

import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.PublicationPolicyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para políticas de publicación en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface PublicationPolicyRepository extends MongoRepository<PublicationPolicyDocument, String> {
}
