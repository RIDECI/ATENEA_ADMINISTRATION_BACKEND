package edu.dosw.rideci.infrastructure.adapters.persistence;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import edu.dosw.rideci.application.port.out.MessageRepositoryPort;
import edu.dosw.rideci.domain.model.valueobjects.Message;
import edu.dosw.rideci.infrastructure.persistence.Repository.MessageMongoRepository;
import edu.dosw.rideci.infrastructure.persistence.Repository.mapper.MessageDocumentMapper;

import java.util.Optional;


/**
 * Adaptador de persistencia para mensajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class MessageRepositoryAdapter implements MessageRepositoryPort {

    private final MessageMongoRepository repo;
    private final MessageDocumentMapper mapper;

    /**
     * Verifica si existe un mensaje por ID
     *
     * @param messageId ID del mensaje
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsById(String messageId) {
        return repo.existsById(messageId);
    }

    /**
     * Guarda un mensaje
     *
     * @param message Mensaje a guardar
     * @return Mensaje guardado
     */
    @Override
    public Message save(Message message) {
        var doc = mapper.toDocument(message);
        var saved = repo.save(doc);
        return mapper.toDomain(saved);
    }

    /**
     * Busca un mensaje por ID
     *
     * @param messageId ID del mensaje
     * @return Mensaje encontrado (opcional)
     */
    @Override
    public Optional<Message> findById(String messageId) {
        return repo.findById(messageId).map(mapper::toDomain);
    }
}
