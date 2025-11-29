package edu.dosw.rideci.infrastructure.adapters.persistence;

import edu.dosw.rideci.application.port.out.ReputationRepositoryPort;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryReputationAdapter implements ReputationRepositoryPort {

    private final Map<Long, List<Rating>> store = new ConcurrentHashMap<>();

    @Override
    public boolean existsById(Long ratingId) {
        if (ratingId == null) return false;
        return store.values().stream()
                .flatMap(List::stream)
                .anyMatch(r -> ratingId.equals(r.getId()));
    }

    @Override
    public Rating saveRating(Rating r) {
        if (r == null) throw new IllegalArgumentException("rating required");
        store.computeIfAbsent(r.getRatedProfileId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(r);
        return r;
    }

    @Override
    public List<Rating> findByRatedProfileId(Long profileId) {
        return List.copyOf(store.getOrDefault(profileId, List.of()));
    }

    @Override
    public double averageForProfile(Long profileId) {
        var list = store.getOrDefault(profileId, List.of());
        if (list.isEmpty()) return 0.0;
        return list.stream().mapToDouble(x -> x.getScore()).average().orElse(0.0);
    }

    @Override
    public long countForProfile(Long profileId) {
        return store.getOrDefault(profileId, List.of()).size();
    }
}
