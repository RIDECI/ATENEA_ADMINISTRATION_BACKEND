package edu.dosw.rideci.application.mapper;

import org.mapstruct.Mapper;

import java.util.List;

import edu.dosw.rideci.domain.model.Driver;
import edu.dosw.rideci.infrastructure.controller.dto.response.DriverDto;

/**
 * Mapper para conversión entre entidades Driver y DTOs en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface DriverMapper {
    /**
     * Convierte entidad Driver a DriverDto
     *
     * @param d Entidad Driver
     * @return DriverDto convertido
     */
    DriverDto toDto(Driver d);
    /**
     * Convierte lista de entidades Driver a lista de DriverDto
     *
     * @param list Lista de entidades Driver
     * @return Lista de DriverDto convertidos
     */
    List<DriverDto> toListDto(List<Driver> list);
}
