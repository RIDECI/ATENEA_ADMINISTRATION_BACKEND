package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.AdminActionDocument;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.AdminAction;

import java.util.List;

/**
 * Mapper para conversión entre AdminAction y AdminActionDocument en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface AdminActionMapper {

    AdminActionMapper INSTANCE = Mappers.getMapper(AdminActionMapper.class);

    /**
     * Convierte AdminActionDocument a AdminAction
     *
     * @param doc Documento de acción de administrador
     * @return Modelo de dominio de acción de administrador
     */
    AdminAction toDomain(AdminActionDocument doc);

    /**
     * Convierte AdminAction a AdminActionDocument
     *
     * @param domain Modelo de dominio de acción de administrador
     * @return Documento de acción de administrador
     */
    AdminActionDocument toDocument(AdminAction domain);

    /**
     * Convierte lista de AdminActionDocument a lista de AdminAction
     *
     * @param docs Lista de documentos de acciones de administrador
     * @return Lista de modelos de dominio de acciones de administrador
     */
    List<AdminAction> toDomainList(List<AdminActionDocument> docs);
}
