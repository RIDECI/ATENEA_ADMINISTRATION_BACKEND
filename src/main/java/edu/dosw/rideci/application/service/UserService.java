package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.events.UserActivatedEvent;
import edu.dosw.rideci.application.events.UserBlockedEvent;
import edu.dosw.rideci.application.events.UserSuspendedEvent;
import edu.dosw.rideci.application.port.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.dosw.rideci.application.port.out.EventPublisher;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.domain.model.User;
import edu.dosw.rideci.application.exceptions.UserNotFoundException;
import edu.dosw.rideci.infrastructure.controller.dto.request.SuspendUserRequestDto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

/**
 * Servicio para gestión de usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserService implements GetUsersUseCase, GetUserDetailUseCase,
        SuspendUserUseCase, ActivateUserUseCase, BlockUserUseCase {

    private final UserRepositoryPort userRepo;
    private final EventPublisher eventPublisher;
    private final AdminActionService adminActionService;
    private static final String USER_NOT_FOUND = "User not found: ";

    /**
     * Lista usuarios con filtros opcionales
     *
     * @param search Término de búsqueda (opcional)
     * @param status Estado del usuario (opcional)
     * @param role Rol del usuario (opcional)
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de usuarios
     */
    @Override
    public List<User> listUsers(String search, String status, String role, int page, int size) {
        if (search != null && !search.isBlank()) return userRepo.searchByName(search);
        if (status != null && !status.isBlank()) return userRepo.findByStatus(status);
        if ((search == null || search.isBlank()) && (status == null || status.isBlank()))
            return userRepo.findAllPaged(page, size);
        return List.of();
    }

    /**
     * Obtiene un usuario por ID
     *
     * @param id ID del usuario
     * @return Usuario encontrado
     * @throws UserNotFoundException Si el usuario no existe
     */
    @Override
    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + id));
    }

    /**
     * Suspende un usuario en el sistema
     *
     * @param userId ID del usuario a suspender
     * @param req Datos de la suspensión
     * @throws IllegalArgumentException Si la solicitud es nula
     */
    @Override
    @Transactional
    public void suspendUser(Long userId, SuspendUserRequestDto req) {
        if (req == null) throw new IllegalArgumentException("Suspend request required");

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));

        if ("SUSPENDED".equalsIgnoreCase(user.getStatus())) {
            return;
        }

        String previousRole = user.getRole();
        if (previousRole != null && !previousRole.isBlank()) {
            user.setPreviousRole(previousRole);
        }

        user.setRole("STUDENT, ADMIN");
        user.setStatus("SUSPENDED");
        userRepo.save(user);

        adminActionService.recordAction(req.getAdminId(), "SUSPEND_USER", "USER",
                String.valueOf(userId), "reason=" + req.getReason() + (previousRole != null ? ",prevRole=" + previousRole : ""));

        UserSuspendedEvent ev = UserSuspendedEvent.builder()
                .suspensionId(UUID.randomUUID().toString())
                .userId(userId)
                .adminId(req.getAdminId())
                .reason(req.getReason())
                .startDate(req.getStartAt() == null ? null : LocalDateTime.parse(req.getStartAt()))
                .endDate(req.getEndAt() == null ? null : LocalDateTime.parse(req.getEndAt()))
                .build();

        eventPublisher.publish(ev, "admin.user.suspended");
    }

    /**
     * Activa un usuario en el sistema
     *
     * @param userId ID del usuario a activar
     * @param adminId ID del administrador que realiza la acción
     */
    @Override
    @Transactional
    public void activateUser(Long userId, Long adminId) {

        User u = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));

        if (u.getPreviousRole() != null && !u.getPreviousRole().isBlank()) {
            u.setRole(u.getPreviousRole());
            u.setPreviousRole(null);
        } else {
            u.setRole(u.getRole() == null ? "STUDENT" : u.getRole());
        }

        u.setStatus("ACTIVE");
        userRepo.save(u);

        adminActionService.recordAction(adminId, "ACTIVATE_USER", "USER",
                String.valueOf(userId), "activated");

        UserActivatedEvent ev = UserActivatedEvent.builder()
                .userId(userId)
                .adminId(adminId)
                .activatedAt(LocalDateTime.now())
                .build();
        eventPublisher.publish(ev, "admin.user.activated");
    }

    /**
     * Bloquea un usuario en el sistema
     *
     * @param userId ID del usuario a bloquear
     * @param adminId ID del administrador que realiza la acción
     * @param reason Motivo del bloqueo
     */
    @Override
    @Transactional
    public void blockUser(Long userId, Long adminId, String reason) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));

        u.setPreviousRole(u.getRole());
        u.setRole("USER");
        u.setStatus("BLOCKED");
        userRepo.save(u);

        adminActionService.recordAction(adminId, "BLOCK_USER", "USER",
                String.valueOf(userId), reason == null ? "blocked_by_admin" : reason);

        UserBlockedEvent ev = UserBlockedEvent.builder()
                .userId(userId)
                .adminId(adminId)
                .reason(reason)
                .blockedAt(LocalDateTime.now())
                .build();
        eventPublisher.publish(ev, "admin.user.blocked");
    }
}
