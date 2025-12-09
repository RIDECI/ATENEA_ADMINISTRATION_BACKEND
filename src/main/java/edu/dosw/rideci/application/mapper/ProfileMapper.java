package edu.dosw.rideci.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import edu.dosw.rideci.domain.model.Profile;
import edu.dosw.rideci.application.events.ProfileEvent;

/**
 * Mapper para conversión entre eventos de perfil y entidades de dominio en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface ProfileMapper {
    /**
     * Convierte un evento de perfil a entidad de dominio Profile
     *
     * @param ev Evento de perfil
     * @return Entidad de dominio Profile
     */
    @Mapping(source = "profileType", target = "profileType")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "profilePictureUrl", target = "profilePictureUrl")
    Profile fromEvent(ProfileEvent ev);
}