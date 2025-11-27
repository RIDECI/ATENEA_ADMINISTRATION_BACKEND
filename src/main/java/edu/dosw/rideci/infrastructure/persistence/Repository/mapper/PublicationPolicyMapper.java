package edu.dosw.rideci.infrastructure.persistence.Repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import edu.dosw.rideci.infrastructure.persistence.Entity.PublicationPolicyDocument;
import edu.dosw.rideci.domain.model.PublicationPolicy;

import java.util.List;

/**
 * Mapper para conversión entre PublicationPolicy y PublicationPolicyDocument en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface PublicationPolicyMapper {

    /**
     * Convierte PublicationPolicyDocument a PublicationPolicy
     *
     * @param doc Documento de política de publicación
     * @return Modelo de dominio de política de publicación
     */
    @Mapping(target = "id", source = "id")
    PublicationPolicy toDomain(PublicationPolicyDocument doc);

    /**
     * Convierte PublicationPolicy a PublicationPolicyDocument
     *
     * @param domain Modelo de dominio de política de publicación
     * @return Documento de política de publicación
     */
    @Mapping(target = "id", source = "id")
    PublicationPolicyDocument toDocument(PublicationPolicy domain);

    /**
     * Convierte lista de PublicationPolicyDocument a lista de PublicationPolicy
     *
     * @param docs Lista de documentos de políticas de publicación
     * @return Lista de modelos de dominio de políticas de publicación
     */
    List<PublicationPolicy> toDomainList(List<PublicationPolicyDocument> docs);
}
