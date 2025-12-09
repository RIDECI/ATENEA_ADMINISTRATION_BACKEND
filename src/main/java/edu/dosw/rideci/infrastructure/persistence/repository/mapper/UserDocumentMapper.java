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
     * UserDocument -> User
     */
    @Mapping(source = "state", target = "status")
    @Mapping(source = "suspensionCount", target = "suspensionCount")
    @Mapping(source = "blocked", target = "blocked")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    User toDomain(UserDocument d);

    /**
     * User -> UserDocument
     */
    @Mapping(source = "status", target = "state")
    @Mapping(source = "suspensionCount", target = "suspensionCount")
    @Mapping(source = "blocked", target = "blocked")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    UserDocument toDocument(User u);

    List<User> toListDomain(List<UserDocument> docs);
}