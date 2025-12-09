package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.port.in.ManageProfileUseCase;
import edu.dosw.rideci.application.port.out.ProfileClientPort;
import edu.dosw.rideci.domain.model.Profile;
import edu.dosw.rideci.infrastructure.controller.ProfilesController;
import edu.dosw.rideci.infrastructure.controller.dto.request.SuspendUserRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfilesControllerTest {

    @InjectMocks
    private ProfilesController controller;

    @Mock
    private ManageProfileUseCase manageUseCase;

    @Mock
    private ProfileClientPort profileClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListProfiles() {
        Profile p = Profile.builder().userId(1L).name("A").build();
        when(manageUseCase.listProfiles(null, null, 0, 20)).thenReturn(List.of(p));

        ResponseEntity<List<Profile>> res = controller.list(null, null, 0, 20);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(1, res.getBody().size());
        assertSame(p, res.getBody().get(0));
        verify(manageUseCase, times(1)).listProfiles(null, null, 0, 20);
    }

    @Test
    void shouldGetByUserIdFound() {
        Profile p = Profile.builder().userId(5L).name("B").build();
        when(manageUseCase.getProfileDetails(5L)).thenReturn(Optional.of(p));

        ResponseEntity<Profile> res = controller.get(5L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(p, res.getBody());
        verify(manageUseCase, times(1)).getProfileDetails(5L);
    }

    @Test
    void shouldGetByUserIdNotFound() {
        when(manageUseCase.getProfileDetails(99L)).thenReturn(Optional.empty());

        ResponseEntity<Profile> res = controller.get(99L);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        verify(manageUseCase, times(1)).getProfileDetails(99L);
    }

    @Test
    void shouldActivateProfile_callsClientAndUseCase() {
        Long userId = 10L;
        Long adminId = 1L;
        String profileType = "DRIVER";

        // No stubbing necessary for void methods; just call and verify interactions
        ResponseEntity<Void> res = controller.activate(userId, adminId, profileType);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(profileClient, times(1)).activateProfilesForUserByType(userId, profileType);
        verify(manageUseCase, times(1)).activateProfile(userId, adminId, profileType);
    }

    @Test
    void shouldSuspendProfile_callsClientAndUseCase() {
        Long userId = 20L;
        SuspendUserRequestDto dto = new SuspendUserRequestDto();
        dto.setAdminId(42L);
        dto.setProfileType("PASSENGER");
        dto.setReason("misconduct");
        dto.setStartAt("2025-12-01T00:00:00");
        dto.setEndAt("2025-12-10T00:00:00");

        ResponseEntity<Void> res = controller.suspend(userId, dto);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(profileClient, times(1)).deactivateProfilesForUserByType(userId, dto.getProfileType());
        verify(manageUseCase, times(1))
                .suspendProfile(userId, dto.getAdminId(), dto.getProfileType(), dto.getReason(), dto.getStartAt(), dto.getEndAt());
    }
}
