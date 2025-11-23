package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.SecurityReportDocument;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio MongoDB para reportes de seguridad en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface SecurityReportMongoRepository extends MongoRepository<SecurityReportDocument, String> {
    boolean existsByRelatedIdAndType(String relatedId, String type);
    List<SecurityReportDocument> findByType(String type);
    List<SecurityReportDocument> findByOccurredAtBetween(LocalDateTime from, LocalDateTime to);
}
