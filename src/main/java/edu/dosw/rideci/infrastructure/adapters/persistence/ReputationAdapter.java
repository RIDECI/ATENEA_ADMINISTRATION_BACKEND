package edu.dosw.rideci.infrastructure.adapters.persistence;

import edu.dosw.rideci.application.port.out.ReputationRepositoryPort;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import edu.dosw.rideci.infrastructure.persistence.entity.RatingDocument;
import edu.dosw.rideci.infrastructure.persistence.repository.RatingMongoRepository;
import edu.dosw.rideci.infrastructure.persistence.repository.mapper.RatingDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador de persistencia para reputación en RideECI
 * Implementa el puerto de salida para operaciones de repositorio de calificaciones
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class ReputationAdapter implements ReputationRepositoryPort {

    private final RatingMongoRepository repo;
    private final RatingDocumentMapper mapper;

    /**
     * Verifica si existe una calificación por ID
     *
     * @param ratingId ID de la calificación
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsById(Long ratingId) {
        if (ratingId == null) return false;
        return repo.findByRatingId(ratingId).isPresent();
    }

    /**
     * Guarda una calificación en el repositorio
     *
     * @param r Calificación a guardar
     * @return Calificación guardada
     */
    @Override
    public Rating saveRating(Rating r) {
        RatingDocument doc = mapper.toDocument(r);
        RatingDocument saved = repo.save(doc);
        return mapper.toDomain(saved);
    }

    /**
     * Calcula el promedio de calificaciones para un perfil
     *
     * @param profileId ID del perfil del usuario
     * @return Promedio de calificaciones (0.0 si no hay calificaciones)
     */
    @Override
    public double averageForProfile(Long profileId) {
        List<RatingDocument> docs = repo.findAllByRatedProfileId(profileId);
        return docs.stream()
                .map(RatingDocument::getScore)
                .filter(s -> s != null)
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Cuenta las calificaciones para un perfil
     *
     * @param profileId ID del perfil del usuario
     * @return Número total de calificaciones recibidas
     */
    @Override
    public long countForProfile(Long profileId) {
        return repo.findAllByRatedProfileId(profileId).size();
    }

    /**
     * Obtiene todas las calificaciones para un perfil
     *
     * @param profileId ID del perfil del usuario
     * @return Lista de calificaciones recibidas por el usuario
     */
    @Override
    public List<Rating> findByRatedProfileId(Long profileId) {
        return mapper.toDomainList(repo.findAllByRatedProfileId(profileId));
    }

}
