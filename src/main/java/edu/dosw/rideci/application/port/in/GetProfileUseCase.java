package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.Profile;
import java.util.Optional;

/**
 * Caso de uso para obtención de perfil individual en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface GetProfileUseCase {
    /**
     * Obtiene un perfil por ID de usuario
     *
     * @param userId ID del usuario
     * @return Perfil encontrado (opcional)
     */
    Optional<Profile> getByUserId(Long userId);
}