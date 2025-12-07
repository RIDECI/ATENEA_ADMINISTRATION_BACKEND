package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.port.out.ReputationRepositoryPort;
import edu.dosw.rideci.application.service.RatingSyncService;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RatingSyncServiceTest {

    @InjectMocks
    private RatingSyncService service;

    @Mock
    private ReputationRepositoryPort reputationRepo;

    @Test
    void shouldCreateRatingDelegatesToRepository() {
        Rating in = Rating.builder()
                .id(null)
                .raterProfileId(100L)
                .ratedProfileId(200L)
                .tripId(777L)
                .score(5)
                .comment("Excelente")
                .createdAt(LocalDateTime.now())
                .build();

        when(reputationRepo.saveRating(any(Rating.class))).thenAnswer(inv -> {
            Rating arg = inv.getArgument(0);
            arg.setId(123L); // simula DB asignando id
            return arg;
        });

        Rating saved = service.createRating(in);

        assertNotNull(saved, "Saved rating no debe ser nulo");
        assertEquals(123L, saved.getId());
        assertEquals(in.getRaterProfileId(), saved.getRaterProfileId());
        assertEquals(in.getRatedProfileId(), saved.getRatedProfileId());
        assertEquals(in.getScore(), saved.getScore());

        verify(reputationRepo, times(1)).saveRating(any(Rating.class));
        verifyNoMoreInteractions(reputationRepo);
    }

    @Test
    void shouldReturnRatingsForProfile() {
        Long profileId = 55L;
        Rating r1 = Rating.builder()
                .id(1L)
                .raterProfileId(200L)
                .ratedProfileId(profileId)
                .tripId(777L)
                .score(4)
                .comment("Ok")
                .createdAt(LocalDateTime.now())
                .build();

        Rating r2 = Rating.builder()
                .id(2L)
                .raterProfileId(201L)
                .ratedProfileId(profileId)
                .tripId(778L)
                .score(5)
                .comment("Muy bien")
                .createdAt(LocalDateTime.now())
                .build();

        when(reputationRepo.findByRatedProfileId(profileId)).thenReturn(List.of(r1, r2));

        List<Rating> result = service.getRatingsForProfile(profileId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(r1, result.get(0));
        assertEquals(r2, result.get(1));
        verify(reputationRepo, times(1)).findByRatedProfileId(profileId);
        verifyNoMoreInteractions(reputationRepo);
    }

    @Test
    void shouldReturnEmptyListWhenNoRatings() {
        Long profileId = 999L;
        when(reputationRepo.findByRatedProfileId(profileId)).thenReturn(List.of());

        List<Rating> result = service.getRatingsForProfile(profileId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reputationRepo, times(1)).findByRatedProfileId(profileId);
        verifyNoMoreInteractions(reputationRepo);
    }
}
