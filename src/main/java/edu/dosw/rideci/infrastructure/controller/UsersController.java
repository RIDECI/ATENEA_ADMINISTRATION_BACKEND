package edu.dosw.rideci.infrastructure.controller;

import edu.dosw.rideci.application.port.in.*;
import edu.dosw.rideci.application.port.in.*;
import edu.dosw.rideci.application.mapper.UserMapper;
import edu.dosw.rideci.infrastructure.controller.dto.Request.SuspendUserRequestDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UsersController {

    private final GetUsersUseCase getUsersUseCase;
    private final GetUserDetailUseCase getUserDetailUseCase;
    private final SuspendUserUseCase suspendUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final BlockUserUseCase blockUserUseCase;
    private final UserMapper userMapper;

    @Operation(summary = "Listar usuarios, filtros opcionales")
    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(userMapper.toListDto(getUsersUseCase.listUsers(search, status, role, page, size)));
    }

    @Operation(summary = "Obtener detalle de un usuario")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userMapper.toDto(getUserDetailUseCase.getUserById(id)));
    }

    @Operation(summary = "Suspender usuario")
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable("id") Long id,
                                            @RequestBody SuspendUserRequestDto dto) {
        suspendUserUseCase.suspendUser(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar usuario")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable("id") Long id,
                                             @RequestParam Long adminId) {
        activateUserUseCase.activateUser(id, adminId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bloquear usuario")
    @PatchMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable("id") Long id,
                                          @RequestParam Long adminId,
                                          @RequestBody(required = false) String reason) {
        blockUserUseCase.blockUser(id, adminId, reason == null ? "blocked_by_admin" : reason);
        return ResponseEntity.noContent().build();
    }
}
