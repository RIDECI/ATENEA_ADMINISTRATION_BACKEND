package edu.dosw.rideci.unit.usecases;

import edu.dosw.rideci.application.service.UserSyncService;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSyncServiceTest {

    @InjectMocks
    private UserSyncService service;

    @Mock
    private UserRepositoryPort userRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateUserSetCreatedAtWhenMissing() {
        User u = new User();
        u.setId(100L);
        u.setCreatedAt(null);

        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = service.createUser(u);

        assertNotNull(saved.getCreatedAt());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void shouldReturnSavedUserWhenCreatedAtPresent() {
        User u = new User();
        u.setId(101L);
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        u.setCreatedAt(now);

        when(userRepo.save(any(User.class))).thenReturn(u);

        User saved = service.createUser(u);
        assertEquals(now, saved.getCreatedAt());
        verify(userRepo, times(1)).save(u);
    }

    @Test
    void shouldThrowWhenUserNull() {
        assertThrows(IllegalArgumentException.class, () -> service.createUser(null));
    }
}
