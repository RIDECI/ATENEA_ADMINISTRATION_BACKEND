package edu.dosw.rideci.unit.controller;

import edu.dosw.rideci.application.port.in.*;
import edu.dosw.rideci.domain.model.User;
import edu.dosw.rideci.infrastructure.controller.UsersController;
import edu.dosw.rideci.infrastructure.controller.dto.Request.SuspendUserRequestDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.UserDto;
import edu.dosw.rideci.application.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UsersControllerTest {

    @InjectMocks
    private UsersController controller;

    @Mock
    private GetUsersUseCase getUsersUseCase;

    @Mock
    private GetUserDetailUseCase getUserDetailUseCase;

    @Mock
    private SuspendUserUseCase suspendUserUseCase;

    @Mock
    private ActivateUserUseCase activateUserUseCase;

    @Mock
    private BlockUserUseCase blockUserUseCase;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListUsers() {
        User u = new User();
        UserDto dto = new UserDto();
        when(getUsersUseCase.listUsers(null, null, null, 0, 20)).thenReturn(List.of(u));
        when(userMapper.toListDto(List.of(u))).thenReturn(List.of(dto));
        ResponseEntity<List<UserDto>> res = controller.listUsers(null, null, null, 0, 20);

        assertEquals(200, res.getStatusCodeValue());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void shouldGetUser() {
        Long id = 10L;
        User u = new User();
        UserDto dto = new UserDto();
        when(getUserDetailUseCase.getUserById(id)).thenReturn(u);
        when(userMapper.toDto(u)).thenReturn(dto);
        ResponseEntity<UserDto> res = controller.getUser(id);

        assertEquals(200, res.getStatusCodeValue());
        assertSame(dto, res.getBody());
    }

    @Test
    void shouldSuspendUser() {
        Long id = 12L;
        SuspendUserRequestDto req = new SuspendUserRequestDto();
        req.setAdminId(1L);
        req.setReason("test");
        ResponseEntity<Void> res = controller.suspendUser(id, req);

        assertEquals(204, res.getStatusCodeValue());
    }

    @Test
    void shouldActivateUser() {
        Long id = 13L;
        Long adminId = 2L;
        ResponseEntity<Void> res = controller.activateUser(id, adminId);

        assertEquals(204, res.getStatusCodeValue());
    }

    @Test
    void shouldBlockUser() {
        Long id = 14L;
        Long adminId = 3L;
        String reason = "bad";
        ResponseEntity<Void> res = controller.blockUser(id, adminId, reason);

        assertEquals(204, res.getStatusCodeValue());
    }
}
