package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.User;
import java.util.List;

/**
 * Caso de uso para listado de usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface GetUsersUseCase {
    /**
     * Lista usuarios con filtros opcionales
     *
     * @param search Término de búsqueda opcional
     * @param status Estado del usuario opcional
     * @param role Rol del usuario opcional
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de usuarios
     */
    List<User> listUsers(String search, String status, String role, int page, int size);
}
