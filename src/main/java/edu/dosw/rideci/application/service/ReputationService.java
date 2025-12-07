package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.events.RatingCreatedEvent;
import edu.dosw.rideci.application.port.in.CreateRatingUseCase;
import edu.dosw.rideci.application.port.out.ReputationRepositoryPort;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de reputación en RideECI
 * Maneja el procesamiento de calificaciones y actualización de reputaciones de usuarios
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReputationService {

    private final CreateRatingUseCase createRatingUseCase;
    private final ReputationRepositoryPort reputationRepo;
    private final UserRepositoryPort userRepo;

    /**
     * Procesa un evento de calificación creada
     * Almacena la calificación y actualiza la reputación del usuario calificado
     *
     * @param e Evento de calificación creada
     */
    @Transactional
    public void handleRatingCreated(RatingCreatedEvent e) {
        if (e == null) {
            log.warn("handleRatingCreated called with null event");
            return;
        }
        if (e.getRatedProfileId() == null || e.getScore() == null) {
            log.warn("handleRatingCreated missing ratedProfileId or score: {}", e);
            return;
        }

        Rating r = Rating.builder()
                .id(e.getRatingId())
                .raterProfileId(e.getRaterProfileId())
                .ratedProfileId(e.getRatedProfileId())
                .tripId(e.getTripId())
                .score(e.getScore())
                .comment(e.getComment())
                .createdAt(e.getCreatedAt())
                .build();

        if (r.getId() != null && reputationRepo.existsById(r.getId())) {
            log.info("Rating {} already exists - ignoring duplicate", r.getId());
            return;
        }

        createRatingUseCase.createRating(r);

        double avg = reputationRepo.averageForProfile(r.getRatedProfileId());
        long count = reputationRepo.countForProfile(r.getRatedProfileId());
        boolean updated = false;
        try {
            updated = userRepo.updateReputationSummary(r.getRatedProfileId(), avg, count);
        } catch (UnsupportedOperationException ex) {
            log.debug("userRepo.updateReputationSummary not supported, fallback to find+save");
        } catch (Exception ex) {
            log.warn("Error calling updateReputationSummary for profile {}: {}", r.getRatedProfileId(), ex.getMessage(), ex);
        }

        if (!updated) {
            userRepo.findById(r.getRatedProfileId()).ifPresent(u -> {
                u.setReputation(avg);
                userRepo.save(u);
                log.info("Updated user {} reputation to {} (count={}) via fallback find+save", r.getRatedProfileId(), avg, count);
            });
        } else {
            log.info("Updated reputation summary for profile {} -> avg={} count={}", r.getRatedProfileId(), avg, count);
        }
    }
}
