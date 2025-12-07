package edu.dosw.rideci.infrastructure.persistence.repository.mapper;

import org.mapstruct.Mapper;
import java.util.List;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import edu.dosw.rideci.infrastructure.persistence.entity.RatingDocument;
import org.mapstruct.Mapping;

/**
 * Mapper para conversión entre Rating y RatingDocument en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface RatingDocumentMapper {

    @Mapping(source = "id", target = "ratingId")
    RatingDocument toDocument(Rating r);

    @Mapping(source = "ratingId", target = "id")
    Rating toDomain(RatingDocument doc);
    List<Rating> toDomainList(List<RatingDocument> docs);
}
