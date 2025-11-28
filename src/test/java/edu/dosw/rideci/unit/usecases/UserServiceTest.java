package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.service.UserService;
import edu.dosw.rideci.application.service.AdminActionService;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.application.port.out.EventPublisher;
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

        var res = service.listUsers("rob", null, null,0,10);
        assertEquals(1, res.size());
        verify(userRepo, times(1)).searchByName("rob");
    }

    @Test
    void shouldListUsersByStatus() {
        User u = new User();
        u.setId(2L);
        u.setStatus("ACTIVE");
        when(userRepo.findByStatus("ACTIVE")).thenReturn(List.of(u));

        var res = service.listUsers(null, "ACTIVE", null,0,10);
        assertEquals(1, res.size());
        verify(userRepo, times(1)).findByStatus("ACTIVE");
    }

    @Test
    void shouldListUsersPaged() {
        User u = new User();
        when(userRepo.findAllPaged(0,10)).thenReturn(List.of(u));
        var res = service.listUsers(null, null, null,0,10);
        assertEquals(1, res.size());
        verify(userRepo, times(1)).findAllPaged(0,10);
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
        service.suspendUser(5L, req);
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("SUSPENDED", saved.getStatus());
        assertEquals("STUDENT, ADMIN", saved.getRole());
        assertEquals("PROFESSOR", saved.getPreviousRole());
        verify(adminActionService, times(1)).recordAction(eq(999L), eq("SUSPEND_USER"), eq("USER"), anyString(), contains("reason="));
        verify(eventPublisher, times(1)).publish(any(UserSuspendedEvent.class), eq("admin.user.suspended"));
    }

    @Test
    void shouldNotSuspendAlreadySuspended() {
        User u = new User();
        u.setId(6L);
        u.setStatus("SUSPENDED");
        when(userRepo.findById(6L)).thenReturn(Optional.of(u));
        SuspendUserRequestDto req = new SuspendUserRequestDto();
        req.setAdminId(1L);
        req.setReason("x");
        service.suspendUser(6L, req);
        verify(userRepo, never()).save(any());
        verify(eventPublisher, never()).publish(any(), anyString());
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
        service.activateUser(7L, 100L);
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("PROFESSOR", saved.getRole());
        assertNull(saved.getPreviousRole());
        assertEquals("ACTIVE", saved.getStatus());
        verify(adminActionService, times(1)).recordAction(eq(100L), eq("ACTIVATE_USER"), eq("USER"), anyString(), anyString());
        verify(eventPublisher, times(1)).publish(any(UserActivatedEvent.class), eq("admin.user.activated"));
    }

    @Test
    void shouldActivateUserWhenNoPreviousRoleSetDefault() {
        User u = new User();
        u.setId(8L);
        u.setRole(null);
        u.setStatus("SUSPENDED");
        when(userRepo.findById(8L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        service.activateUser(8L, 101L);
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(cap.capture());
        User saved = cap.getValue();
        assertNotNull(saved.getRole());
        assertEquals("ACTIVE", saved.getStatus());
    }

    @Test
    void shouldBlockUser() {
        User u = new User();
        u.setId(11L);
        u.setRole("STUDENT");
        u.setStatus("ACTIVE");
        when(userRepo.findById(11L)).thenReturn(Optional.of(u));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        service.blockUser(11L, 200L, "fraud");

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("BLOCKED", saved.getStatus());
        assertEquals("USER", saved.getRole());
        assertEquals("STUDENT", saved.getPreviousRole());
        verify(adminActionService, times(1)).recordAction(eq(200L), eq("BLOCK_USER"), eq("USER"), anyString(), contains("fraud"));
        verify(eventPublisher, times(1)).publish(any(UserBlockedEvent.class), eq("admin.user.blocked"));
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

        service.suspendUser(20L, req);

        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("SUSPENDED", saved.getStatus());
        assertEquals("STUDENT, ADMIN", saved.getRole());

        ArgumentCaptor<UserSuspendedEvent> evCap = ArgumentCaptor.forClass(UserSuspendedEvent.class);
        verify(eventPublisher).publish(evCap.capture(), eq("admin.user.suspended"));
        UserSuspendedEvent ev = evCap.getValue();
        assertEquals(LocalDateTime.parse("2025-01-01T10:00:00"), ev.getStartDate());
        assertEquals(LocalDateTime.parse("2025-01-03T18:30:00"), ev.getEndDate());
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
        service.blockUser(33L, 300L, null);
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(cap.capture());
        User saved = cap.getValue();
        assertEquals("BLOCKED", saved.getStatus());
        verify(adminActionService, times(1))
                .recordAction(eq(300L), eq("BLOCK_USER"), eq("USER"), anyString(), eq("blocked_by_admin"));
        verify(eventPublisher, times(1)).publish(any(UserBlockedEvent.class), eq("admin.user.blocked"));
    }
}
