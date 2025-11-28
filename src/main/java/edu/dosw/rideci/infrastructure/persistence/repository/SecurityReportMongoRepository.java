package edu.dosw.rideci.infrastructure.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import edu.dosw.rideci.infrastructure.persistence.entity.SecurityReportDocument;

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
