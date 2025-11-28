package edu.dosw.rideci.infrastructure.persistence.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import edu.dosw.rideci.infrastructure.persistence.entity.DriverDocument;
import edu.dosw.rideci.domain.model.Driver;

/**
 * Mapper para conversión entre Driver y DriverDocument en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface DriverDocumentMapper {


    /**
     * Convierte DriverDocument a Driver
     *
     * @param doc Documento de conductor
     * @return Modelo de dominio de conductor
     */
    @Mapping(target = "driverId", source = "driverId")
    Driver toDomain(DriverDocument doc);

    /**
     * Convierte Driver a DriverDocument
     *
     * @param domain Modelo de dominio de conductor
     * @return Documento de conductor
     */
    @Mapping(target = "driverId", source = "driverId")
    DriverDocument toDocument(Driver domain);
}
