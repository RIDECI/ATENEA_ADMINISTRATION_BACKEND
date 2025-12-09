package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.events.ProfileEvent;
import edu.dosw.rideci.application.events.UserBlockedEvent;
import edu.dosw.rideci.application.events.UserSuspendedEvent;
import edu.dosw.rideci.application.mapper.ProfileMapper;
import edu.dosw.rideci.application.port.in.ManageProfileUseCase;
import edu.dosw.rideci.application.port.out.ProfileClientPort;
import edu.dosw.rideci.application.port.out.ProfileRepositoryPort;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.application.port.out.EventPublisher;
import edu.dosw.rideci.domain.model.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestión de perfiles de usuario en RideECI
 * Maneja activación, desactivación, suspensión y operaciones CRUD de perfiles
 *
 * @author RideECI
 * @version 1.0
 */
@Service("profileService")
@RequiredArgsConstructor
public class ProfileService implements ManageProfileUseCase {

    private final ProfileRepositoryPort profileRepo;
    private final ProfileMapper profileMapper;
    private final ProfileClientPort profileClient;
    private final UserRepositoryPort userRepo;
    private final EventPublisher eventPublisher;
    private final AdminActionService adminActionService;
    private static final String PROFILE_TYPE_PREFIX = "profileType=";
    private static final String PROFILE_TYPE_ALL = "ALL";

    /**
     * Obtiene un perfil por ID de usuario
     *
     * @param userId ID del usuario
     * @return Perfil encontrado
     */
    public Optional<Profile> getByUserId(Long userId) {
        return profileRepo.findByUserId(userId);
    }


    /**
     * Obtiene detalles de un perfil por ID de usuario
     *
     * @param userId ID del usuario
     * @return Detalles del perfil
     */
    @Override
    public Optional<Profile> getProfileDetails(Long userId) {
        return getByUserId(userId);
    }

    /**
     * Lista perfiles con filtros opcionales
     *
     * @param search Término de búsqueda
     * @param profileType Tipo de perfil
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de perfiles
     */
    @Override
    public List<Profile> listProfiles(String search, String profileType, int page, int size) {
        if (search != null && !search.isBlank()) return profileRepo.searchByName(search);
        if (profileType != null && !profileType.isBlank()) return profileRepo.findByProfileType(profileType);
        return profileRepo.findAllPaged(page, size);
    }

    /**
     * Crea o actualiza un perfil desde un evento
     *
     * @param ev Evento de perfil
     * @return Perfil creado o actualizado
     */
    @Override
    @Transactional
    public Profile upsertFromEvent(ProfileEvent ev) {
        Profile p = profileMapper.fromEvent(ev);
        if (p.getState() == null) p.setState("ACTIVE");
        return profileRepo.save(p);
    }

    /**
     * Activa un perfil de usuario
     *
     * @param userId ID del usuario
     * @param adminId ID del administrador
     * @param profileType Tipo de perfil a activar (opcional)
     */
    @Override
    @Transactional
    public void activateProfile(Long userId, Long adminId, String profileType) {
        profileRepo.findByUserId(userId).ifPresent(p -> {
            p.setState("ACTIVE");
            profileRepo.save(p);
        });

        if (profileType != null && !profileType.isBlank()) {
            profileClient.activateProfilesForUserByType(userId, profileType);
        } else {
            profileClient.activateProfilesForUser(userId);
        }

        adminActionService.recordAction(adminId, profileType == null ? "ACTIVATE_USER" : "ACTIVATE_PROFILE_TYPE",
                "USER", String.valueOf(userId),
                profileType == null ? "activated" : PROFILE_TYPE_PREFIX + profileType);
    }


    /**
     * Desactiva un perfil de usuario
     *
     * @param userId ID del usuario
     * @param adminId ID del administrador
     * @param profileType Tipo de perfil a desactivar (opcional)
     */
    @Override
    @Transactional
    public void deactivateProfile(Long userId, Long adminId, String profileType) {
        profileRepo.findByUserId(userId).ifPresent(p -> {
            p.setState("INACTIVE");
            profileRepo.save(p);
        });

        if (profileType != null && !profileType.isBlank()) {
            profileClient.deactivateProfilesForUserByType(userId, profileType);
            adminActionService.recordAction(adminId, "DEACTIVATE_PROFILE_TYPE", "USER", String.valueOf(userId),
                    PROFILE_TYPE_PREFIX + profileType);
        } else {
            profileClient.deactivateProfilesForUser(userId);
            adminActionService.recordAction(adminId, "DEACTIVATE_USER", "USER", String.valueOf(userId),
                    "deactivated");
        }
    }

    /**
     * Suspende un perfil de usuario
     *
     * @param userId ID del usuario
     * @param adminId ID del administrador
     * @param profileType Tipo de perfil a suspender (opcional)
     * @param reason Motivo de la suspensión
     * @param startAt Fecha de inicio (opcional)
     * @param endAt Fecha de fin (opcional)
     */
    @Override
    @Transactional
    public void suspendProfile(Long userId, Long adminId, String profileType, String reason, String startAt, String endAt) {

        String profileDetail = PROFILE_TYPE_PREFIX + (profileType == null ? PROFILE_TYPE_ALL : profileType);
        String details = profileDetail + ",reason=" + reason;
        adminActionService.recordAction(
                adminId,
                profileType == null ? "SUSPEND_USER" : "SUSPEND_PROFILE_TYPE",
                "USER",
                String.valueOf(userId),
                details);

        profileRepo.findByUserId(userId).ifPresent(p -> {
            p.setState("SUSPENDED");
            profileRepo.save(p);
        });

        if (profileType != null && !profileType.isBlank()) {
            profileClient.deactivateProfilesForUserByType(userId, profileType);
        } else {
            profileClient.deactivateProfilesForUser(userId);
        }

        long nextCount;
        try {
            nextCount = userRepo.incrementSuspensionCount(userId, adminId, reason);
        } catch (RuntimeException ex) {
            nextCount = -1;
        }

        UserSuspendedEvent suspendedEv = UserSuspendedEvent.builder()
                .suspensionId(UUID.randomUUID().toString())
                .userId(userId)
                .adminId(adminId)
                .reason(reason == null ? "suspended_by_admin" : reason)
                .startDate(startAt == null ? null : LocalDateTime.parse(startAt))
                .endDate(endAt == null ? null : LocalDateTime.parse(endAt))
                .build();
        eventPublisher.publish(suspendedEv, "admin.user.suspended");

        if (nextCount >= 3) {
            boolean changed = userRepo.blockUser(userId, adminId, "suspension_threshold_reached");
            if (changed) {
                UserBlockedEvent blockedEv = UserBlockedEvent.builder()
                        .userId(userId)
                        .adminId(adminId)
                        .reason("suspension_threshold_reached")
                        .blockedAt(LocalDateTime.now())
                        .build();
                eventPublisher.publish(blockedEv, "admin.user.blocked");

                adminActionService.recordAction(adminId, "AUTO_BLOCK_USER", "USER", String.valueOf(userId),
                        "suspensions=" + nextCount);
            }
        }
    }
}
