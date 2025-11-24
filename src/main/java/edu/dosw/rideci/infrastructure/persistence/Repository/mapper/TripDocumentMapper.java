package edu.dosw.rideci.infrastructure.persistence.Repository.mapper;

import org.mapstruct.Mapper;
import edu.dosw.rideci.infrastructure.persistence.Entity.TripDocument;
import edu.dosw.rideci.domain.model.TripMonitor;
import java.util.List;

/**
 * Mapper para conversión entre TripMonitor y TripDocument en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface TripDocumentMapper {

    /**
     * Convierte TripDocument a TripMonitor
     *
     * @param doc Documento de viaje
     * @return Modelo de dominio de viaje
     */
    TripMonitor toDomain(TripDocument doc);

    /**
     * Convierte TripMonitor a TripDocument
     *
     * @param domain Modelo de dominio de viaje
     * @return Documento de viaje
     */
    TripDocument toDocument(TripMonitor domain);

    /**
     * Convierte lista de TripDocument a lista de TripMonitor
     *
     * @param docs Lista de documentos de viaje
     * @return Lista de modelos de dominio de viaje
     */
    List<TripMonitor> toDomainList(List<TripDocument> docs);
}
