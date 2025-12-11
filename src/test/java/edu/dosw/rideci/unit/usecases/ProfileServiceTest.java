package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.events.ProfileEvent;
import edu.dosw.rideci.application.events.UserSuspendedEvent;
import edu.dosw.rideci.application.port.out.EventPublisher;
import edu.dosw.rideci.application.port.out.ProfileClientPort;
import edu.dosw.rideci.application.port.out.ProfileRepositoryPort;
import edu.dosw.rideci.application.mapper.ProfileMapper;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.application.service.AdminActionService;
import edu.dosw.rideci.domain.model.Profile;
import edu.dosw.rideci.domain.model.enums.ProfileType;
import edu.dosw.rideci.application.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @InjectMocks
    private ProfileService service;

    @Mock
    private ProfileRepositoryPort profileRepo;

    @Mock
    private ProfileClientPort profileClient;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private AdminActionService adminActionService;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private UserRepositoryPort userRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListProfilesBySearch() {
        Profile p = Profile.builder().userId(1L).name("Rob").build();
        when(profileRepo.searchByName("rob")).thenReturn(List.of(p));

        var res = service.listProfiles("rob", null, 0, 10);
        assertEquals(1, res.size());
        verify(profileRepo, times(1)).searchByName("rob");
    }

    @Test
    void shouldListProfilesByType() {
        Profile p = Profile.builder().userId(2L).profileType("DRIVER").build();
        when(profileRepo.findByProfileType("DRIVER")).thenReturn(List.of(p));

        var res = service.listProfiles(null, "DRIVER", 0, 10);
        assertEquals(1, res.size());
        verify(profileRepo, times(1)).findByProfileType("DRIVER");
    }

    @Test
    void shouldListProfilesPaged() {
        Profile p = Profile.builder().userId(3L).build();
        when(profileRepo.findAllPaged(0, 10)).thenReturn(List.of(p));

        var res = service.listProfiles(null, null, 0, 10);
        assertEquals(1, res.size());
        verify(profileRepo, times(1)).findAllPaged(0, 10);
    }

    @Test
    void shouldGetByUserId() {
        Profile p = Profile.builder().userId(5L).name("X").build();
        when(profileRepo.findByUserId(5L)).thenReturn(Optional.of(p));

        var res = service.getByUserId(5L);
        assertTrue(res.isPresent());
        assertEquals(5L, res.get().getUserId());
    }

    @Test
    void shouldUpsertFromEventCreatesProfile() {
        ProfileEvent ev = ProfileEvent.builder()
                .userId(10L)
                .name("Robinson")
                .email("r@test.com")
                .phoneNumber("300")
                .profileType(ProfileType.DRIVER)
                .build();

        Profile mapped = Profile.builder()
                .userId(10L)
                .name("Robinson")
                .email("r@test.com")
                .profileType("DRIVER")
                .build();

        when(profileMapper.fromEvent(ev)).thenReturn(mapped);
        when(profileRepo.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        Profile saved = service.upsertFromEvent(ev);

        assertNotNull(saved);
        assertEquals(10L, saved.getUserId());
        assertEquals("Robinson", saved.getName());
        assertEquals("DRIVER", saved.getProfileType());
        verify(profileRepo, times(1)).save(any(Profile.class));
    }

    @Test
    void shouldActivateProfile_updateLocalAndCallClientByType() {
        Long userId = 20L;
        Profile existing = Profile.builder().userId(userId).state("INACTIVE").build();
        when(profileRepo.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(profileRepo.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        service.activateProfile(userId, 1L, "DRIVER");

        ArgumentCaptor<Profile> cap = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepo).save(cap.capture());
        assertEquals("ACTIVE", cap.getValue().getState());

        verify(profileClient, times(1)).activateProfilesForUserByType(userId, "DRIVER");
        verify(profileClient, never()).activateProfilesForUser(anyLong());
    }

    @Test
    void shouldActivateProfile_callClientWithoutType() {
        Long userId = 21L;
        when(profileRepo.findByUserId(userId)).thenReturn(Optional.empty());

        service.activateProfile(userId, 2L, null);

        verify(profileRepo, never()).save(any(Profile.class));
        verify(profileClient, times(1)).activateProfilesForUser(userId);
    }

    @Test
    void shouldDeactivateProfile_updateLocalAndCallClientByType() {
        Long userId = 30L;
        Profile existing = Profile.builder().userId(userId).state("ACTIVE").build();
        when(profileRepo.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(profileRepo.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        service.deactivateProfile(userId, 5L, "PASSENGER");

        ArgumentCaptor<Profile> cap = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepo).save(cap.capture());
        assertEquals("INACTIVE", cap.getValue().getState());

        verify(profileClient, times(1)).deactivateProfilesForUserByType(userId, "PASSENGER");
        verify(profileClient, never()).deactivateProfilesForUser(anyLong());
    }

    @Test
    void shouldDeactivateProfile_callClientWithoutType() {
        Long userId = 31L;
        when(profileRepo.findByUserId(userId)).thenReturn(Optional.empty());

        service.deactivateProfile(userId, 6L, null);

        verify(profileRepo, never()).save(any(Profile.class));
        verify(profileClient, times(1)).deactivateProfilesForUser(userId);
    }


    @Test
    void shouldSuspendProfile() {
        Long userId = 40L;
        Profile existing = Profile.builder().userId(userId).state("ACTIVE").build();
        when(profileRepo.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(profileRepo.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        service.suspendProfile(userId, 5L, "DRIVER", "reasonX", null, null);

        ArgumentCaptor<Profile> cap = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepo).save(cap.capture());
        assertEquals("SUSPENDED", cap.getValue().getState());

        verify(profileClient, times(1)).deactivateProfilesForUserByType(userId, "DRIVER");

        ArgumentCaptor<String> detailsCap = ArgumentCaptor.forClass(String.class);
        verify(adminActionService).recordAction(eq(5L), eq("SUSPEND_PROFILE_TYPE"), eq("USER"),
                eq(String.valueOf(userId)), detailsCap.capture());
        assertTrue(detailsCap.getValue().contains("profileType="));
        verify(eventPublisher).publish(any(UserSuspendedEvent.class), eq("admin.user.suspended"));
    }

    @Test
    void shouldSuspendProfileWithoutType() {
        Long userId = 41L;
        when(profileRepo.findByUserId(userId)).thenReturn(Optional.empty());

        service.suspendProfile(userId, 6L, null, "reasonY", null, null);

        verify(profileRepo, never()).save(any(Profile.class));
        verify(profileClient, times(1)).deactivateProfilesForUser(userId);
        verify(adminActionService).recordAction(eq(6L), eq("SUSPEND_USER"), eq("USER"), eq(String.valueOf(userId)), anyString());
        verify(eventPublisher).publish(any(UserSuspendedEvent.class), eq("admin.user.suspended"));
    }


    @Test
    void shouldGetProfileDetailsDelegatesToGetByUserId() {
        Profile p = Profile.builder().userId(99L).name("Detalle").build();
        when(profileRepo.findByUserId(99L)).thenReturn(Optional.of(p));
        var res = service.getProfileDetails(99L);

        assertTrue(res.isPresent());
        assertEquals(99L, res.get().getUserId());
        verify(profileRepo, times(1)).findByUserId(99L);
    }

}
