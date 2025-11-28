package edu.dosw.rideci.infrastructure.persistence.repository;

import edu.dosw.rideci.infrastructure.persistence.entity.AdminActionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para acciones de administrador en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface AdminActionRepository extends MongoRepository<AdminActionDocument, String> {
}
