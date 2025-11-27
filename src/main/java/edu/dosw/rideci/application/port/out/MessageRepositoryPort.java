package edu.dosw.rideci.application.port.out;

import edu.dosw.rideci.domain.model.valueobjects.Message;

import java.util.Optional;

/**
 * Puerto de salida para operaciones de repositorio de mensajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface MessageRepositoryPort {

    /**
     * Verifica si existe un mensaje por ID
     *
     * @param messageId ID del mensaje
     * @return true si existe, false en caso contrario
     */
    boolean existsById(String messageId);

    /**
     * Guarda un mensaje
     *
     * @param message Mensaje a guardar
     * @return Mensaje guardado
     */
    Message save(Message message);

    /**
     * Busca un mensaje por ID
     *
     * @param messageId ID del mensaje
     * @return Mensaje encontrado (opcional)
     */
    Optional<Message> findById(String messageId);
}
