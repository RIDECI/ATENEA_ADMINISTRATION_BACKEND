package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.events.UserActivatedEvent;
import edu.dosw.rideci.application.events.UserBlockedEvent;
import edu.dosw.rideci.application.events.UserSuspendedEvent;
import edu.dosw.rideci.application.port.in.*;
import edu.dosw.rideci.application.port.out.ProfileClientPort;
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
    private final ProfileClientPort profileClient;
    private static final String USER_NOT_FOUND = "User not found: ";

    /**
     * Lista usuarios con filtros opcionales
     *
     * @param search Término de búsqueda (opcional)
     * @param status Estado del usuario (opcional)
     * @param role   Rol del usuario (opcional)
     * @param page   Página a consultar
     * @param size   Tamaño de la página
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
     * @param req    Datos de la suspensión
     * @throws IllegalArgumentException Si la solicitud es nula
     */
    @Override
    @Transactional
    public void suspendUser(Long userId, SuspendUserRequestDto req) {
        if (req == null) throw new IllegalArgumentException("Suspend request required");

        if (req.getProfileType() != null && (req.getAccountOnly() == null || !req.getAccountOnly())) {
            handleProfileTypeSuspend(userId, req);
            return;
        }

        handleAccountSuspend(userId, req);
    }

    /**
     * Manejador de los perfiles suspendidos
     * @param userId
     * @param req
     */
    private void handleProfileTypeSuspend(Long userId, SuspendUserRequestDto req) {

        adminActionService.recordAction(req.getAdminId(), "SUSPEND_PROFILE_TYPE", "USER",
                String.valueOf(userId), "profileType=" + req.getProfileType() + ",reason=" + req.getReason());

        profileClient.deactivateProfilesForUserByType(userId, req.getProfileType());

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
     * Manejador de las cuentas suspendidas
     * @param userId
     * @param req
     */
    private void handleAccountSuspend(Long userId, SuspendUserRequestDto req) {
        User user = loadExistingUser(userId);
        persistPreviousRoleIfMissing(user);
        markUserSuspendedLocally(user);
        long nextCount = userRepo.incrementSuspensionCount(userId, req.getAdminId(), req.getReason());
        User updated = loadExistingUser(userId);
        recordAdminSuspendAction(req, userId, nextCount);
        publishUserSuspendedEvent(req, userId);
        profileClient.deactivateProfilesForUser(userId);

        if (updated.isBlocked()) {
            publishAutoBlockEvents(req.getAdminId(), userId, nextCount);
        }
    }

    /**
     * Activa un usuario en el sistema
     *
     * @param userId  ID del usuario a activar
     * @param adminId ID del administrador que realiza la acción
     */
    @Override
    @Transactional
    public void activateUser(Long userId, Long adminId, String profileType) {
        if (profileType != null && !profileType.isBlank()) {
            handleProfileTypeActivate(userId, adminId, profileType);
            return;
        }
        handleAccountActivate(userId, adminId);
    }

    /**
     * Manejador de los perfiles activos
     * @param userId
     * @param adminId
     * @param profileType
     */
    private void handleProfileTypeActivate(Long userId, Long adminId, String profileType) {
        adminActionService.recordAction(adminId, "ACTIVATE_PROFILE_TYPE", "USER",
                String.valueOf(userId), "profileType=" + profileType);

        profileClient.activateProfilesForUserByType(userId, profileType);

        UserActivatedEvent ev = UserActivatedEvent.builder()
                .userId(userId)
                .adminId(adminId)
                .activatedAt(LocalDateTime.now())
                .build();
        eventPublisher.publish(ev, "admin.user.activated");
    }

    /**
     * Manejador de las cuentas activas
     * @param userId
     * @param adminId
     */
    private void handleAccountActivate(Long userId, Long adminId) {
        User u = loadExistingUser(userId);
        restorePreviousRoleIfAny(u);

        if (u.isBlocked()) {
            u.setBlocked(false);
        }

        u.setStatus("ACTIVE");
        userRepo.save(u);

        adminActionService.recordAction(adminId, "ACTIVATE_USER", "USER",
                String.valueOf(userId), "activated");

        profileClient.activateProfilesForUser(userId);

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
     * @param userId  ID del usuario a bloquear
     * @param adminId ID del administrador que realiza la acción
     * @param reason  Motivo del bloqueo
     */
    @Override
    @Transactional
    public void blockUser(Long userId, Long adminId, String reason) {
        User u = loadExistingUser(userId);
        persistPreviousRoleIfMissing(u);
        userRepo.save(u);

        boolean changed = userRepo.blockUser(userId, adminId, reason);

        adminActionService.recordAction(adminId, "BLOCK_USER", "USER",
                String.valueOf(userId), reason == null ? "blocked_by_admin" : reason);

        if (changed) {
            profileClient.deactivateProfilesForUser(userId);

            UserBlockedEvent ev = UserBlockedEvent.builder()
                    .userId(userId)
                    .adminId(adminId)
                    .reason(reason)
                    .blockedAt(LocalDateTime.now())
                    .build();
            eventPublisher.publish(ev, "admin.user.blocked");
        }
    }

    /**
     * Carga un usuario existente verificando su existencia
     *
     * @param userId ID del usuario a cargar
     * @return Usuario encontrado
     * @throws UserNotFoundException Si el usuario no existe
     */
    private User loadExistingUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
    }

    /**
     * Preserva el rol anterior del usuario si no está establecido
     * Utilizado antes de cambios de estado para permitir restauración
     *
     * @param user Usuario a procesar
     */
    private void persistPreviousRoleIfMissing(User user) {
        if (user.getPreviousRole() == null || user.getPreviousRole().isBlank()) {
            user.setPreviousRole(user.getRole());
        }
    }

    /**
     * Marca un usuario como suspendido en el repositorio local
     *
     * @param user Usuario a suspender
     */
    private void markUserSuspendedLocally(User user) {
        user.setStatus("SUSPENDED");
        userRepo.save(user);
    }

    /**
     * Registra la acción de suspensión en el servicio de auditoría
     *
     * @param req Datos de la suspensión
     * @param userId ID del usuario suspendido
     * @param nextCount Número de suspensión (contador)
     */
    private void recordAdminSuspendAction(SuspendUserRequestDto req, Long userId, long nextCount) {
        adminActionService.recordAction(req.getAdminId(), "SUSPEND_USER", "USER",
                String.valueOf(userId), "reason=" + req.getReason() + ",suspensions=" + nextCount);
    }

    /**
     * Publica evento de usuario suspendido en el bus de eventos
     *
     * @param req Datos de la suspensión
     * @param userId ID del usuario suspendido
     */
    private void publishUserSuspendedEvent(SuspendUserRequestDto req, Long userId) {
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
     * Publica eventos automáticos de bloqueo cuando se alcanza el umbral de suspensiones
     *
     * @param adminId ID del administrador
     * @param userId ID del usuario bloqueado automáticamente
     * @param nextCount Número de suspensión que triggeró el bloqueo
     */
    private void publishAutoBlockEvents(Long adminId, Long userId, long nextCount) {
        UserBlockedEvent b = UserBlockedEvent.builder()
                .userId(userId)
                .adminId(adminId)
                .reason("suspension_threshold_reached")
                .blockedAt(LocalDateTime.now())
                .build();
        eventPublisher.publish(b, "admin.user.blocked");

        adminActionService.recordAction(adminId, "AUTO_BLOCK_USER", "USER",
                String.valueOf(userId), "suspensions=" + nextCount);
    }

    /**
     * Restaura el rol anterior del usuario durante la activación
     * Si no existe rol anterior, establece un rol por defecto
     *
     * @param u Usuario a restaurar
     */
    private void restorePreviousRoleIfAny(User u) {
        if (u.getPreviousRole() != null && !u.getPreviousRole().isBlank()) {
            u.setRole(u.getPreviousRole());
            u.setPreviousRole(null);
        } else {
            u.setRole(u.getRole() == null ? "STUDENT" : u.getRole());
        }
    }
}
