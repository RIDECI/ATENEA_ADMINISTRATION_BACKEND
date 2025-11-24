package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository;

import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.AdminActionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para acciones de administrador en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface AdminActionRepository extends MongoRepository<AdminActionDocument, String> {
}
