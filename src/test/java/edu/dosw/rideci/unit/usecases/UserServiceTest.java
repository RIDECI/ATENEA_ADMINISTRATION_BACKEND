package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.service.UserService;
import edu.dosw.rideci.application.service.AdminActionService;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.application.port.out.EventPublisher;
import edu.dosw.rideci.application.port.out.ProfileClientPort;
import edu.dosw.rideci.domain.model.User;
import edu.dosw.rideci.infrastructure.controller.dto.request.SuspendUserRequestDto;
import edu.dosw.rideci.application.events.UserSuspendedEvent;
import edu.dosw.rideci.application.events.UserActivatedEvent;
import edu.dosw.rideci.application.events.UserBlockedEvent;
import edu.dosw.rideci.application.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepositoryPort userRepo;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AdminActionService adminActionService;

    @Mock
    private ProfileClientPort profileClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListUsersBySearch() {
        User u = new User();
        u.setId(1L);
        u.setName("Rob");
        when(userRepo.searchByName("rob")).thenReturn(List.of(u));

        var res = service.listUsers("rob", null, null, 0, 10);
        assertEquals(1, res.size());
        verify(userRepo, times(1)).searchByName("rob");
    }

    @Test
    void shouldListUsersByStatus() {
        User u = new User();
        u.setId(2L);
        u.setStatus("ACTIVE");
        when(userRepo.findByStatus("ACTIVE")).thenReturn(List.of(u));

        var res = service.listUsers(null, "ACTIVE", null, 0, 10);
        assertEquals(1, res.size());
        verify(userRepo, times(1)).findByStatus("ACTIVE");
    }

    @Test
    void shouldListUsersPaged() {
        User u = new User();
        when(userRepo.findAllPaged(0, 10)).thenReturn(List.of(u));
        var res = service.listUsers(null, null, null, 0, 10);
        assertEquals(1, res.size());
        verify(userRepo, times(1)).findAllPaged(0, 10);
    }

    @Test
    void shouldGetUserByIdSuccess() {
        User u = new User();
        u.setId(10L);
        when(userRepo.findById(10L)).thenReturn(Optional.of(u));

        var res = service.getUserById(10L);
        assertNotNull(res);
        assertEquals(10L, res.getId());
    }

    @Test
    void shouldThrowWhenGetUserNotFound() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUserById(99L));
    }

    @Test
    void shouldSuspendUserNormally() {
        User u = new User();
        u.setId(5L);
        u.setRole("PROFESSOR");
        u.setStatus("ACTIVE");
        SuspendUserRequestDto req = new SuspendUserRequestDto();
        req.setAdminId(999L);
        req.setReason("bad");
        req.setStartAt(null);
        req.setEndAt(null);
        when(userRepo.findById(5L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.incrementSuspensionCount(eq(5L), anyLong(), anyString())).thenReturn(1L);
        service.suspendUser(5L, req);

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("SUSPENDED", saved.getStatus());
        assertEquals("PROFESSOR", saved.getRole());
        assertEquals("PROFESSOR", saved.getPreviousRole());
        ArgumentCaptor<Long> adminCap = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> actionCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityIdCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detailsCap = ArgumentCaptor.forClass(String.class);
        verify(adminActionService).recordAction(adminCap.capture(), actionCap.capture(),
                entityCap.capture(), entityIdCap.capture(), detailsCap.capture());

        assertEquals(999L, adminCap.getValue());
        assertEquals("SUSPEND_USER", actionCap.getValue());
        assertEquals("USER", entityCap.getValue());
        assertTrue(detailsCap.getValue().contains("reason="));
        ArgumentCaptor<Object> evCap = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> routingCap = ArgumentCaptor.forClass(String.class);
        verify(eventPublisher).publish(evCap.capture(), routingCap.capture());
        assertEquals("admin.user.suspended", routingCap.getValue());
        assertTrue(evCap.getValue() instanceof UserSuspendedEvent);
        verify(profileClient, times(1)).deactivateProfilesForUser(5L);
    }

    @Test
    void shouldNotSuspendAlreadySuspended() {
        User u = new User();
        u.setId(6L);
        u.setStatus("SUSPENDED");
        when(userRepo.findById(6L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.incrementSuspensionCount(eq(6L), anyLong(), anyString())).thenReturn(1L);

        SuspendUserRequestDto req = new SuspendUserRequestDto();
        req.setAdminId(1L);
        req.setReason("x");

        service.suspendUser(6L, req);

        // we expect it to be recorded again (save + increment + event + profile deactivation)
        verify(userRepo, times(1)).save(any(User.class));
        verify(userRepo, times(1)).incrementSuspensionCount(eq(6L), eq(1L), anyString());
        verify(eventPublisher, times(1)).publish(any(UserSuspendedEvent.class), eq("admin.user.suspended"));
        verify(profileClient, times(1)).deactivateProfilesForUser(6L);
    }


    @Test
    void shouldActivateUserRestorePreviousRole() {
        User u = new User();
        u.setId(7L);
        u.setRole("STUDENT, ADMIN");
        u.setPreviousRole("PROFESSOR");
        u.setStatus("SUSPENDED");
        when(userRepo.findById(7L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        service.activateUser(7L, 100L, null);
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("PROFESSOR", saved.getRole());
        assertNull(saved.getPreviousRole());
        assertEquals("ACTIVE", saved.getStatus());
        ArgumentCaptor<Long> adminCap = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> actionCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityIdCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detailsCap = ArgumentCaptor.forClass(String.class);
        verify(adminActionService).recordAction(adminCap.capture(), actionCap.capture(),
                entityCap.capture(), entityIdCap.capture(), detailsCap.capture());

        assertEquals(100L, adminCap.getValue());
        assertEquals("ACTIVATE_USER", actionCap.getValue());
        assertEquals("USER", entityCap.getValue());

        ArgumentCaptor<Object> evCap = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> routingCap = ArgumentCaptor.forClass(String.class);
        verify(eventPublisher).publish(evCap.capture(), routingCap.capture());
        assertEquals("admin.user.activated", routingCap.getValue());
        assertTrue(evCap.getValue() instanceof UserActivatedEvent);
        verify(profileClient, times(1)).activateProfilesForUser(7L);
    }

    @Test
    void shouldActivateUserWhenNoPreviousRoleSetDefault() {
        User u = new User();
        u.setId(8L);
        u.setRole(null);
        u.setStatus("SUSPENDED");
        when(userRepo.findById(8L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.activateUser(8L, 101L, null);

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(cap.capture());
        User saved = cap.getValue();
        assertNotNull(saved.getRole());
        assertEquals("ACTIVE", saved.getStatus());

        verify(profileClient, times(1)).activateProfilesForUser(8L);
    }

    @Test
    void shouldBlockUser() {
        User u = new User();
        u.setId(11L);
        u.setRole("STUDENT");
        u.setStatus("ACTIVE");
        when(userRepo.findById(11L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.blockUser(11L, 200L, "fraud")).thenReturn(true);
        service.blockUser(11L, 200L, "fraud");
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("STUDENT", saved.getPreviousRole());
        verify(userRepo, times(1)).blockUser(11L, 200L, "fraud");
        ArgumentCaptor<Long> adminCap = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> actionCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityIdCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detailsCap = ArgumentCaptor.forClass(String.class);

        verify(adminActionService).recordAction(adminCap.capture(), actionCap.capture(),
                entityCap.capture(), entityIdCap.capture(), detailsCap.capture());

        assertEquals(200L, adminCap.getValue());
        assertEquals("BLOCK_USER", actionCap.getValue());
        assertTrue(detailsCap.getValue().contains("fraud"));

        verify(eventPublisher, times(1)).publish(any(UserBlockedEvent.class), eq("admin.user.blocked"));
        verify(profileClient, times(1)).deactivateProfilesForUser(11L);
    }

    @Test
    void shouldThrowSuspendWhenReqNull() {
        assertThrows(IllegalArgumentException.class, () -> service.suspendUser(1L, null));
    }

    @Test
    void shouldSuspendUserParseDatesAndPublishEvent() {
        User u = new User();
        u.setId(20L);
        u.setRole("TEACHER");
        u.setStatus("ACTIVE");
        SuspendUserRequestDto req = new SuspendUserRequestDto();
        req.setAdminId(42L);
        req.setReason("cheating");
        req.setStartAt("2025-01-01T10:00:00");
        req.setEndAt("2025-01-03T18:30:00");
        when(userRepo.findById(20L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.incrementSuspensionCount(eq(20L), anyLong(), anyString())).thenReturn(1L);

        service.suspendUser(20L, req);
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("SUSPENDED", saved.getStatus());
        assertEquals("TEACHER", saved.getRole());
        ArgumentCaptor<Object> evCap = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> routingCap = ArgumentCaptor.forClass(String.class);
        verify(eventPublisher).publish(evCap.capture(), routingCap.capture());
        assertEquals("admin.user.suspended", routingCap.getValue());
        assertTrue(evCap.getValue() instanceof UserSuspendedEvent);

        UserSuspendedEvent ev = (UserSuspendedEvent) evCap.getValue();
        assertEquals(LocalDateTime.parse("2025-01-01T10:00:00"), ev.getStartDate());
        assertEquals(LocalDateTime.parse("2025-01-03T18:30:00"), ev.getEndDate());

        verify(profileClient, times(1)).deactivateProfilesForUser(20L);
    }

    @Test
    void shouldSuspendUserDoesNotSetPreviousRole() {
        User u = new User();
        u.setId(21L);
        u.setRole(null);
        u.setStatus("ACTIVE");
        SuspendUserRequestDto req = new SuspendUserRequestDto();
        req.setAdminId(77L);
        req.setReason("x");
        when(userRepo.findById(21L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.incrementSuspensionCount(eq(21L), anyLong(), anyString())).thenReturn(1L);
        service.suspendUser(21L, req);

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(cap.capture());
        User saved = cap.getValue();
        assertNull(saved.getPreviousRole(), "previousRole must remain null when original role is null");
        assertEquals("SUSPENDED", saved.getStatus());
    }

    @Test
    void shouldBlockUserUsesDefaultReason() {
        User u = new User();
        u.setId(33L);
        u.setRole("STUDENT");
        u.setStatus("ACTIVE");
        when(userRepo.findById(33L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.blockUser(33L, 300L, null)).thenReturn(true);
        service.blockUser(33L, 300L, null);
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("STUDENT", saved.getPreviousRole());
        ArgumentCaptor<Long> adminCap = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> actionCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityIdCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detailsCap = ArgumentCaptor.forClass(String.class);

        verify(adminActionService).recordAction(adminCap.capture(), actionCap.capture(),
                entityCap.capture(), entityIdCap.capture(), detailsCap.capture());

        assertEquals(300L, adminCap.getValue());
        assertEquals("BLOCK_USER", actionCap.getValue());
        assertEquals("USER", entityCap.getValue());
        assertEquals("blocked_by_admin", detailsCap.getValue());

        verify(eventPublisher, times(1)).publish(any(UserBlockedEvent.class), eq("admin.user.blocked"));
    }


    @Test
    void shouldSuspendProfileTypeOnly() {
        User u = new User();
        u.setId(44L);
        u.setRole("STUDENT");
        u.setStatus("ACTIVE");
        SuspendUserRequestDto req = new SuspendUserRequestDto();
        req.setAdminId(500L);
        req.setReason("driver_issue");
        req.setProfileType("DRIVER");
        req.setAccountOnly(false);
        when(userRepo.findById(44L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        service.suspendUser(44L, req);
        verify(userRepo, never()).incrementSuspensionCount(anyLong(), anyLong(), anyString());
        verify(profileClient, times(1)).deactivateProfilesForUserByType(44L, "DRIVER");

        ArgumentCaptor<Object> evCap = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> routingCap = ArgumentCaptor.forClass(String.class);
        verify(eventPublisher).publish(evCap.capture(), routingCap.capture());
        assertEquals("admin.user.suspended", routingCap.getValue());
    }

    @Test
    void shouldAutoBlockWhenSuspensionThresholdReached() {
        User uBefore = new User();
        uBefore.setId(55L);
        uBefore.setRole("STUDENT");
        uBefore.setStatus("ACTIVE");
        uBefore.setBlocked(false);

        User uAfter = new User();
        uAfter.setId(55L);
        uAfter.setRole("STUDENT");
        uAfter.setStatus("BLOCKED");
        uAfter.setBlocked(true);

        SuspendUserRequestDto req = new SuspendUserRequestDto();
        req.setAdminId(600L);
        req.setReason("repeat_offense");

        when(userRepo.findById(55L)).thenReturn(Optional.of(uBefore), Optional.of(uAfter));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.incrementSuspensionCount(eq(55L), anyLong(), anyString())).thenReturn(3L); // threshold reached

        service.suspendUser(55L, req);
        verify(userRepo, times(1)).incrementSuspensionCount(eq(55L), eq(600L), anyString());
        ArgumentCaptor<Object> evCap = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> routingCap = ArgumentCaptor.forClass(String.class);
        verify(eventPublisher, atLeastOnce()).publish(evCap.capture(), routingCap.capture());
        assertTrue(routingCap.getAllValues().stream().anyMatch("admin.user.blocked"::equals));
        ArgumentCaptor<Long> adminCap = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> actionCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> entityIdCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detailsCap = ArgumentCaptor.forClass(String.class);

        verify(adminActionService, atLeastOnce()).recordAction(adminCap.capture(), actionCap.capture(),
                entityCap.capture(), entityIdCap.capture(), detailsCap.capture());
        assertTrue(actionCap.getAllValues().contains("AUTO_BLOCK_USER"));
        verify(profileClient, times(1)).deactivateProfilesForUser(55L);
    }
}
