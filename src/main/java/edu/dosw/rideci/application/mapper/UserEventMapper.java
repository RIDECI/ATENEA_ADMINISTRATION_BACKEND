package edu.dosw.rideci.application.mapper;

import edu.dosw.rideci.application.events.UserEvent;
import edu.dosw.rideci.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper: UserEvent -> domain.User
 * Ajustado a la forma actual de edu.dosw.rideci.domain.model.User
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEventMapper {

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "state", target = "status")
    User toDomain(UserEvent ev);
}
