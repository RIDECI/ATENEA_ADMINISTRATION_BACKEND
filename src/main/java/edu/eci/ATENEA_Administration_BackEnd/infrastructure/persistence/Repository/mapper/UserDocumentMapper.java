package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository.mapper;

import org.mapstruct.Mapper;
import java.util.List;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.UserDocument;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.User;

/**
 * Mapper para conversión entre User y UserDocument en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface UserDocumentMapper {

    /**
     * Convierte UserDocument a User
     *
     * @param d Documento de usuario
     * @return Modelo de dominio de usuario
     */
    User toDomain(UserDocument d);

    /**
     * Convierte User a UserDocument
     *
     * @param u Modelo de dominio de usuario
     * @return Documento de usuario
     */
    UserDocument toDocument(User u);

    /**
     * Convierte lista de UserDocument a lista de User
     *
     * @param docs Lista de documentos de usuario
     * @return Lista de modelos de dominio de usuario
     */
    List<User> toListDomain(List<UserDocument> docs);
}
