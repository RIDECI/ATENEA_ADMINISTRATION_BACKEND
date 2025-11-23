package edu.eci.ATENEA_Administration_BackEnd.application.port.out;

import edu.eci.ATENEA_Administration_BackEnd.domain.model.User;
import java.util.List;
import java.util.Optional;


/**
 * Puerto de salida para operaciones de repositorio de usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface UserRepositoryPort {

    /**
     * Obtiene todos los usuarios paginados
     *
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de usuarios paginados
     */
    List<User> findAllPaged(int page, int size);

    /**
     * Busca usuarios por nombre
     *
     * @param search Término de búsqueda
     * @return Lista de usuarios
     */
    List<User> searchByName(String search);

    /**
     * Busca usuarios por estado
     *
     * @param status Estado del usuario
     * @return Lista de usuarios
     */
    List<User> findByStatus(String status);

    /**
     * Busca un usuario por ID
     *
     * @param id ID del usuario
     * @return Usuario encontrado (opcional)
     */
    Optional<User> findById(Long id);

    /**
     * Guarda un usuario
     *
     * @param u Usuario a guardar
     * @return Usuario guardado
     */
    User save(User u);

    /**
     * Actualiza el estado de un usuario
     *
     * @param userId ID del usuario
     * @param newStatus Nuevo estado
     * @return Usuario actualizado
     */
    User updateStatus(Long userId, String newStatus);

    /**
     * Obtiene todos los usuarios
     *
     * @return Lista de todos los usuarios
     */
    List<User> findAll();
}
