package edu.dosw.rideci.infrastructure.persistence.Repository.mapper;

import org.mapstruct.Mapper;
import edu.dosw.rideci.infrastructure.persistence.Entity.MessageDocument;
import edu.dosw.rideci.domain.model.valueobjects.Message;
import java.util.List;

/**
 * Mapper para conversión entre Message y MessageDocument en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface MessageDocumentMapper {
    Message toDomain(MessageDocument doc);
    MessageDocument toDocument(Message domain);
    List<Message> toDomainList(List<MessageDocument> docs);
}
