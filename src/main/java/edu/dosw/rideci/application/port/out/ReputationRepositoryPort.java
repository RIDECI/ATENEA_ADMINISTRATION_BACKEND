package edu.dosw.rideci.application.port.out;

import edu.dosw.rideci.domain.model.valueobjects.Rating;

import java.util.List;

/**
 * Puerto de salida para operaciones de repositorio de reputación en RideECI
 * Maneja el almacenamiento y consulta de calificaciones y cálculos de reputación
 *
 * @author RideECI
 * @version 1.0
 */
public interface ReputationRepositoryPort {

    /**
     * Verifica si existe una calificación por ID
     *
     * @param ratingId ID de la calificación
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long ratingId);

    /**
     * Guarda una calificación
     *
     * @param r Calificación a guardar
     * @return Calificación guardada
     */
    Rating saveRating(Rating r);

    /**
     * Calcula el promedio de calificaciones para un perfil
     *
     * @param profileId ID del perfil
     * @return Promedio de calificaciones
     */
    double averageForProfile(Long profileId);

    /**
     * Cuenta el número de calificaciones para un perfil
     *
     * @param profileId ID del perfil
     * @return Número de calificaciones
     */
    long countForProfile(Long profileId);

    /**
     * Busca calificaciones por ID de perfil calificado
     *
     * @param profileId ID del perfil calificado
     * @return Lista de calificaciones
     */
    List<Rating> findByRatedProfileId(Long profileId);
}

