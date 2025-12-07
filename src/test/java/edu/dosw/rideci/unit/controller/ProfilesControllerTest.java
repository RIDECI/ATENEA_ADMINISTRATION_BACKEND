package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.mapper.RatingMapper;
import edu.dosw.rideci.application.port.in.GetProfileUseCase;
import edu.dosw.rideci.application.port.in.GetProfilesUseCase;
import edu.dosw.rideci.application.port.in.GetRatingsByProfileUseCase;
import edu.dosw.rideci.application.port.in.ManageProfileUseCase;
import edu.dosw.rideci.domain.model.Profile;
import edu.dosw.rideci.domain.model.valueobjects.Rating;
import edu.dosw.rideci.infrastructure.controller.ProfilesController;
import edu.dosw.rideci.infrastructure.controller.dto.response.RatingResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfilesControllerTest {

    @InjectMocks
    private ProfilesController controller;

    @Mock
    private GetProfilesUseCase listUseCase;

    @Mock
    private GetProfileUseCase getUseCase;

    @Mock
    private ManageProfileUseCase manageUseCase;

    @Mock
    private GetRatingsByProfileUseCase getRatingsByProfile;

    @Mock
    private RatingMapper ratingMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListProfiles() {
        Profile p = Profile.builder().userId(1L).name("A").build();
        when(listUseCase.listProfiles(null, null, 0, 20)).thenReturn(List.of(p));

        ResponseEntity<List<Profile>> res = controller.list(null, null, 0, 20);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
        assertSame(p, res.getBody().get(0));
    }

    @Test
    void shouldGetByUserIdFound() {
        Profile p = Profile.builder().userId(5L).name("B").build();
        when(getUseCase.getByUserId(5L)).thenReturn(Optional.of(p));

        ResponseEntity<Profile> res = controller.getByUserId(5L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(p, res.getBody());
    }

    @Test
    void shouldGetByUserIdNotFound() {
        when(getUseCase.getByUserId(99L)).thenReturn(Optional.empty());
        ResponseEntity<Profile> res = controller.getByUserId(99L);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void shouldActivateProfile() {
        ResponseEntity<Void> res = controller.activateProfile(10L, 1L, "DRIVER");
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(manageUseCase, times(1)).activateProfile(10L, 1L, "DRIVER");
    }

    @Test
    void shouldDeactivateProfile() {
        ResponseEntity<Void> res = controller.deactivateProfile(11L, 2L, null);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(manageUseCase, times(1)).deactivateProfile(11L, 2L, null);
    }

    @Test
    void shouldReturnRatingsForProfile() {
        Long profileId = 55L;
        Rating r = Rating.builder()
                .id(123L)
                .raterProfileId(200L)
                .ratedProfileId(profileId)
                .tripId(777L)
                .score(5)
                .comment("ok")
                .createdAt(LocalDateTime.now())
                .build();

        RatingResponseDto dto = RatingResponseDto.builder()
                .id("abc")
                .ratingId(123L)
                .raterProfileId(200L)
                .ratedProfileId(profileId)
                .tripId(777L)
                .score(5)
                .comment("ok")
                .createdAt(r.getCreatedAt())
                .build();

        when(getRatingsByProfile.getRatingsForProfile(profileId)).thenReturn(List.of(r));
        when(ratingMapper.toListDto(List.of(r))).thenReturn(List.of(dto));

        ResponseEntity<List<RatingResponseDto>> res = controller.getRatingsForProfile(profileId);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(1, res.getBody().size());
        assertEquals(dto, res.getBody().get(0));
    }
}
