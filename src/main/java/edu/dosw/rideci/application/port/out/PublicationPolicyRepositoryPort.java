package edu.dosw.rideci.application.port.out;

import edu.dosw.rideci.infrastructure.persistence.Entity.PublicationPolicyDocument;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de repositorio de políticas de publicación en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface PublicationPolicyRepositoryPort {

    /**
     * Guarda una política de publicación
     *
     * @param doc Política de publicación a guardar
     * @return Política de publicación guardada
     */
    PublicationPolicyDocument save(PublicationPolicyDocument doc);

    /**
     * Busca una política de publicación por ID
     *
     * @param id ID de la política
     * @return Política de publicación encontrada (opcional)
     */
    Optional<PublicationPolicyDocument> findById(String id);

    /**
     * Obtiene todas las políticas de publicación
     *
     * @return Lista de todas las políticas de publicación
     */
    List<PublicationPolicyDocument> findAll();

    /**
     * Elimina una política de publicación por ID
     *
     * @param id ID de la política a eliminar
     */
    void deleteById(String id);
}
