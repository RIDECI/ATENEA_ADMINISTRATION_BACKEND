package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.valueobjects.Rating;
import java.util.List;


/**
 * Caso de uso para obtención de calificaciones por perfil en RideECI
 * Permite consultar las calificaciones recibidas por un usuario específico
 *
 * @author RideECI
 * @version 1.0
 */
public interface GetRatingsByProfileUseCase {
    /**
     * Obtiene las calificaciones asociadas a un perfil de usuario
     *
     * @param profileId ID del perfil del usuario calificado
     * @return Lista de calificaciones recibidas por el usuario
     */
    List<Rating> getRatingsForProfile(Long profileId);
}
