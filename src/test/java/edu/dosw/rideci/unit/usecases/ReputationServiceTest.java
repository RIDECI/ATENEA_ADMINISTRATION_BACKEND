package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.events.RatingCreatedEvent;
import edu.dosw.rideci.application.port.out.ReputationRepositoryPort;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.application.service.ReputationService;
import edu.dosw.rideci.domain.model.User;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReputationServiceTest {

    @InjectMocks
    private ReputationService service;

    @Mock
    private ReputationRepositoryPort reputationRepo;

    @Mock
    private UserRepositoryPort userRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldIgnoreNullEvent() {
        service.handleRatingCreated(null);
        verifyNoInteractions(reputationRepo, userRepo);
    }

    @Test
    void shouldIgnoreMissingFields() {
        RatingCreatedEvent ev1 = RatingCreatedEvent.builder()
                .ratingId(1L)
                .raterProfileId(100L)
                .ratedProfileId(null)
                .tripId(200L)
                .score(4)
                .comment("ok")
                .createdAt(LocalDateTime.now())
                .build();

        service.handleRatingCreated(ev1);
        verifyNoInteractions(reputationRepo, userRepo);

        reset(reputationRepo, userRepo);

        RatingCreatedEvent ev2 = RatingCreatedEvent.builder()
                .ratingId(2L)
                .raterProfileId(101L)
                .ratedProfileId(500L)
                .tripId(201L)
                .score(null)
                .comment("ok")
                .createdAt(LocalDateTime.now())
                .build();

        service.handleRatingCreated(ev2);
        verifyNoInteractions(reputationRepo, userRepo);
    }

    @Test
    void shouldSkipDuplicate() {
        Long ratingId = 10L;
        RatingCreatedEvent ev = RatingCreatedEvent.builder()
                .ratingId(ratingId)
                .raterProfileId(100L)
                .ratedProfileId(500L)
                .tripId(200L)
                .score(5)
                .comment("nice")
                .createdAt(LocalDateTime.now())
                .build();

        when(reputationRepo.existsById(ratingId)).thenReturn(true);

        service.handleRatingCreated(ev);

        verify(reputationRepo, times(1)).existsById(ratingId);
        verify(reputationRepo, never()).saveRating(any(Rating.class));
        verifyNoInteractions(userRepo);
    }

    @Test
    void shouldSaveAndUpdateWhenUpdateSupported() {
        Long ratingId = 21L;
        Long ratedProfileId = 5000L;
        RatingCreatedEvent ev = RatingCreatedEvent.builder()
                .ratingId(ratingId)
                .raterProfileId(100L)
                .ratedProfileId(ratedProfileId)
                .tripId(200L)
                .score(4)
                .comment("nice")
                .createdAt(LocalDateTime.now())
                .build();

        when(reputationRepo.existsById(ratingId)).thenReturn(false);
        when(reputationRepo.averageForProfile(ratedProfileId)).thenReturn(4.0);
        when(reputationRepo.countForProfile(ratedProfileId)).thenReturn(3L);
        when(userRepo.updateReputationSummary(ratedProfileId, 4.0, 3L)).thenReturn(true);

        service.handleRatingCreated(ev);

        ArgumentCaptor<Rating> ratingCap = ArgumentCaptor.forClass(Rating.class);
        verify(reputationRepo, times(1)).saveRating(ratingCap.capture());
        Rating saved = ratingCap.getValue();
        assertEquals(ratingId, saved.getId());
        assertEquals(ratedProfileId, saved.getRatedProfileId());
        assertEquals(4, saved.getScore()); // Rating.valueobject uses integer score

        verify(reputationRepo, times(1)).averageForProfile(ratedProfileId);
        verify(reputationRepo, times(1)).countForProfile(ratedProfileId);
        verify(userRepo, times(1)).updateReputationSummary(ratedProfileId, 4.0, 3L);
        verify(userRepo, never()).findById(anyLong());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void shouldFallbackWhenUpdateThrows() {
        Long ratingId = 31L;
        Long ratedProfileId = 6000L;
        RatingCreatedEvent ev = RatingCreatedEvent.builder()
                .ratingId(ratingId)
                .raterProfileId(110L)
                .ratedProfileId(ratedProfileId)
                .tripId(210L)
                .score(3)
                .comment("meh")
                .createdAt(LocalDateTime.now())
                .build();

        when(reputationRepo.existsById(ratingId)).thenReturn(false);
        when(reputationRepo.averageForProfile(ratedProfileId)).thenReturn(3.0);
        when(reputationRepo.countForProfile(ratedProfileId)).thenReturn(1L);
        when(userRepo.updateReputationSummary(ratedProfileId, 3.0, 1L))
                .thenThrow(new UnsupportedOperationException("not supported"));
        User u = new User();
        u.setId(ratedProfileId);
        u.setReputation(0.0);
        when(userRepo.findById(ratedProfileId)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.handleRatingCreated(ev);

        verify(reputationRepo).saveRating(any(Rating.class));
        verify(reputationRepo).averageForProfile(ratedProfileId);
        verify(reputationRepo).countForProfile(ratedProfileId);

        verify(userRepo).findById(ratedProfileId);
        ArgumentCaptor<User> userCap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userCap.capture());
        User saved = userCap.getValue();
        assertEquals(ratedProfileId, saved.getId());
        assertEquals(3.0, saved.getReputation());
    }

    @Test
    void shouldFallbackWhenUpdateReturnsFalse() {
        Long ratingId = 41L;
        Long ratedProfileId = 7000L;
        RatingCreatedEvent ev = RatingCreatedEvent.builder()
                .ratingId(ratingId)
                .raterProfileId(120L)
                .ratedProfileId(ratedProfileId)
                .tripId(220L)
                .score(2) // integer
                .comment("bad")
                .createdAt(LocalDateTime.now())
                .build();

        when(reputationRepo.existsById(ratingId)).thenReturn(false);
        when(reputationRepo.averageForProfile(ratedProfileId)).thenReturn(2.0);
        when(reputationRepo.countForProfile(ratedProfileId)).thenReturn(5L);
        when(userRepo.updateReputationSummary(ratedProfileId, 2.0, 5L)).thenReturn(false);

        User u = new User();
        u.setId(ratedProfileId);
        u.setReputation(4.0);
        when(userRepo.findById(ratedProfileId)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.handleRatingCreated(ev);

        verify(reputationRepo).saveRating(any(Rating.class));
        verify(reputationRepo).averageForProfile(ratedProfileId);
        verify(reputationRepo).countForProfile(ratedProfileId);

        verify(userRepo).findById(ratedProfileId);
        ArgumentCaptor<User> userCap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userCap.capture());
        assertEquals(2.0, userCap.getValue().getReputation());
    }
}
