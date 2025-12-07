package edu.dosw.rideci.infrastructure.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import edu.dosw.rideci.infrastructure.persistence.entity.ProfileDocument;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio MongoDB para perfiles en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface ProfileMongoRepository extends MongoRepository<ProfileDocument, String> {
    /**
     * Busca un perfil por ID de usuario
     *
     * @param userId ID del usuario
     * @return Perfil encontrado (opcional)
     */
    Optional<ProfileDocument> findByUserId(Long userId);

    /**
     * Busca perfiles por nombre (búsqueda case-insensitive)
     *
     * @param q Término de búsqueda
     * @return Lista de perfiles que coinciden
     */
    List<ProfileDocument> findByNameContainingIgnoreCase(String q);

    /**
     * Busca perfiles por tipo
     *
     * @param profileType Tipo de perfil
     * @return Lista de perfiles del tipo especificado
     */
    List<ProfileDocument> findByProfileType(String profileType);
}