package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.port.in.CreateRatingUseCase;
import edu.dosw.rideci.application.port.in.GetRatingsByProfileUseCase;
import edu.dosw.rideci.application.port.out.ReputationRepositoryPort;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para sincronización de calificaciones en RideECI
 * Implementa los casos de uso para creación y consulta de calificaciones
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RatingSyncService implements CreateRatingUseCase, GetRatingsByProfileUseCase {

    private final ReputationRepositoryPort reputationRepo;

    /**
     * Crea una nueva calificación en el sistema
     *
     * @param r Calificación a crear
     * @return Calificación creada con identificador asignado
     */
    @Override
    public Rating createRating(Rating r) {
        return reputationRepo.saveRating(r);
    }

    /**
     * Obtiene las calificaciones asociadas a un perfil de usuario
     *
     * @param profileId ID del perfil del usuario calificado
     * @return Lista de calificaciones recibidas por el usuario
     */
    @Override
    public List<Rating> getRatingsForProfile(Long profileId) {
        return reputationRepo.findByRatedProfileId(profileId);
    }
}
