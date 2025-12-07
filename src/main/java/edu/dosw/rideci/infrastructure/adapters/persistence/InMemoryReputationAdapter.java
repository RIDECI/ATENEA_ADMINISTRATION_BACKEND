package edu.dosw.rideci.infrastructure.adapters.persistence;

import edu.dosw.rideci.application.port.out.ReputationRepositoryPort;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador en memoria para reputación en RideECI
 * Implementación temporal para pruebas y desarrollo
 *
 * @author RideECI
 * @version 1.0
 */
@Component
public class InMemoryReputationAdapter implements ReputationRepositoryPort {

    private final Map<Long, List<Rating>> store = new ConcurrentHashMap<>();

    /**
     * Verifica si existe una calificación por ID
     *
     * @param ratingId ID de la calificación
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsById(Long ratingId) {
        if (ratingId == null) return false;
        return store.values().stream()
                .flatMap(List::stream)
                .anyMatch(r -> ratingId.equals(r.getId()));
    }


    /**
     * Guarda una calificación en memoria
     *
     * @param r Calificación a guardar
     * @return Calificación guardada
     */
    @Override
    public Rating saveRating(Rating r) {
        if (r == null) throw new IllegalArgumentException("rating required");
        store.computeIfAbsent(r.getRatedProfileId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(r);
        return r;
    }

    /**
     * Busca calificaciones por ID de perfil calificado
     *
     * @param profileId ID del perfil calificado
     * @return Lista de calificaciones (copia inmodificable)
     */
    @Override
    public List<Rating> findByRatedProfileId(Long profileId) {
        return List.copyOf(store.getOrDefault(profileId, List.of()));
    }

    /**
     * Calcula el promedio de calificaciones para un perfil
     *
     * @param profileId ID del perfil
     * @return Promedio de calificaciones
     */
    @Override
    public double averageForProfile(Long profileId) {
        var list = store.getOrDefault(profileId, List.of());
        if (list.isEmpty()) return 0.0;
        return list.stream().mapToDouble(x -> x.getScore()).average().orElse(0.0);
    }

    /**
     * Cuenta el número de calificaciones para un perfil
     *
     * @param profileId ID del perfil
     * @return Número de calificaciones
     */
    @Override
    public long countForProfile(Long profileId) {
        return store.getOrDefault(profileId, List.of()).size();
    }
}
