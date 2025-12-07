package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.valueobjects.Rating;

/**
 * Caso de uso para creación de calificaciones en RideECI
 * Permite registrar nuevas calificaciones entre usuarios del sistema
 *
 * @author RideECI
 * @version 1.0
 */
public interface CreateRatingUseCase {
    /**
     * Crea una nueva calificación en el sistema
     *
     * @param r Calificación a crear
     * @return Calificación creada con identificador asignado
     */
    Rating createRating(Rating r);
}
