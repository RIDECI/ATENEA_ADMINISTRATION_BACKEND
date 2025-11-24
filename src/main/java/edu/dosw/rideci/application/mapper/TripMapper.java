package edu.dosw.rideci.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

import edu.dosw.rideci.domain.model.TripMonitor;
import edu.dosw.rideci.infrastructure.controller.dto.Response.TripListItemDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.TripDetailDto;

/**
 * Mapper para conversión entre entidades TripMonitor y DTOs en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface TripMapper {
    TripMapper INSTANCE = Mappers.getMapper(TripMapper.class);
    /**
     * Convierte entidad TripMonitor a TripListItemDto
     *
     * @param t Entidad TripMonitor
     * @return TripListItemDto convertido
     */
    TripListItemDto toListItem(TripMonitor t);
    /**
     * Convierte entidad TripMonitor a TripDetailDto
     *
     * @param t Entidad TripMonitor
     * @return TripDetailDto convertido
     */
    TripDetailDto toDetail(TripMonitor t);
    /**
     * Convierte lista de entidades TripMonitor a lista de TripListItemDto
     *
     * @param list Lista de entidades TripMonitor
     * @return Lista de TripListItemDto convertidos
     */
    List<TripListItemDto> toListItems(List<TripMonitor> list);
}
