package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.User;

/**
 * Caso de uso para obtención de detalle de usuario en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface GetUserDetailUseCase {
    /**
     * Obtiene un usuario por ID
     *
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    User getUserById(Long id);
}
