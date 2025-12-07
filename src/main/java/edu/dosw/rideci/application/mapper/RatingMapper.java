package edu.dosw.rideci.application.mapper;

import org.mapstruct.Mapper;
import java.util.List;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import edu.dosw.rideci.infrastructure.controller.dto.response.RatingResponseDto;
import org.mapstruct.Mapping;

/**
 * Mapper para conversión entre entidades Rating y DTOs de respuesta en RideECI
 * Transforma objetos de dominio del sistema de reputación a DTOs para exposición en APIs REST
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface RatingMapper {
    @Mapping(source = "id", target = "ratingId")
    RatingResponseDto toDto(Rating rating);
    List<RatingResponseDto> toListDto(List<Rating> ratings);
}