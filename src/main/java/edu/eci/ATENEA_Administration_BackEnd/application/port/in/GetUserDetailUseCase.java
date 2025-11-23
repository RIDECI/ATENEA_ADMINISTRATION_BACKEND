package edu.eci.ATENEA_Administration_BackEnd.application.port.in;

import edu.eci.ATENEA_Administration_BackEnd.domain.model.User;

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
