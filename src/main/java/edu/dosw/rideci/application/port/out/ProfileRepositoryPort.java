package edu.dosw.rideci.application.port.out;

import edu.dosw.rideci.domain.model.Profile;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de repositorio de perfiles en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface ProfileRepositoryPort {
    /**
     * Guarda un perfil
     *
     * @param profile Perfil a guardar
     * @return Perfil guardado
     */
    Profile save(Profile profile);

    /**
     * Busca un perfil por ID de usuario
     *
     * @param userId ID del usuario
     * @return Perfil encontrado (opcional)
     */
    Optional<Profile> findByUserId(Long userId);

    /**
     * Obtiene todos los perfiles paginados
     *
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de perfiles paginados
     */
    List<Profile> findAllPaged(int page, int size);

    /**
     * Busca perfiles por nombre
     *
     * @param q Término de búsqueda
     * @return Lista de perfiles que coinciden
     */
    List<Profile> searchByName(String q);

    /**
     * Busca perfiles por tipo
     *
     * @param profileType Tipo de perfil
     * @return Lista de perfiles del tipo especificado
     */
    List<Profile> findByProfileType(String profileType);
}