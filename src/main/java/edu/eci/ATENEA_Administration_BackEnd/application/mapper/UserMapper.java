package edu.eci.ATENEA_Administration_BackEnd.application.mapper;

import org.mapstruct.Mapper;
import java.util.List;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.User;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller.dto.Response.UserDto;

/**
 * Mapper para conversión entre entidades User y DTOs en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Convierte entidad User a UserDto
     *
     * @param u Entidad User
     * @return UserDto convertido
     */
    UserDto toDto(User u);

    /**
     * Convierte lista de entidades User a lista de UserDto
     *
     * @param users Lista de entidades User
     * @return Lista de UserDto convertidos
     */
    List<UserDto> toListDto(List<User> users);
}
