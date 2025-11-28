package edu.dosw.rideci.infrastructure.persistence.repository.mapper;

import org.mapstruct.Mapper;
import java.util.List;
import edu.dosw.rideci.infrastructure.persistence.entity.UserDocument;
import edu.dosw.rideci.domain.model.User;
import org.mapstruct.Mapping;

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
    @Mapping(target = "status", source = "state")
    User toDomain(UserDocument d);

    /**
     * Convierte User a UserDocument
     *
     * @param u Modelo de dominio de usuario
     * @return Documento de usuario
     */
    @Mapping(target = "state", source = "status")
    UserDocument toDocument(User u);

    /**
     * Convierte lista de UserDocument a lista de User
     *
     * @param docs Lista de documentos de usuario
     * @return Lista de modelos de dominio de usuario
     */
    List<User> toListDomain(List<UserDocument> docs);
}
