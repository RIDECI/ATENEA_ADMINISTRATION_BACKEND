package edu.dosw.rideci.infrastructure.adapters.messaging;

import edu.dosw.rideci.application.events.ProfileActivatedEvent;
import edu.dosw.rideci.application.events.ProfileSuspendedEvent;
import edu.dosw.rideci.application.port.out.ProfileClientPort;
import edu.dosw.rideci.application.port.out.ProfileCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adaptador de mensajería para comandos de perfil en RideECI
 * Publica eventos para activar/desactivar perfiles de usuario
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class ProfileEventPublisherAdapter implements ProfileClientPort {

    private final ProfileCommandPort publisher;

    /**
     * Publica evento para desactivar perfiles de un usuario
     *
     * @param userId ID del usuario
     */
    @Override
    public void deactivateProfilesForUser(Long userId) {

        ProfileSuspendedEvent ev = ProfileSuspendedEvent.builder()
                .suspensionId(UUID.randomUUID().toString())
                .userId(userId)
                .adminId(null)
                .profileType(null)
                .reason("deactivated_by_admin")
                .startAt(LocalDateTime.now())
                .endAt(null)
                .build();
        publisher.publishProfileSuspended(ev, "profile.suspended");
    }

    /**
     * Publica evento para activar perfiles de un usuario
     *
     * @param userId ID del usuario
     */
    @Override
    public void activateProfilesForUser(Long userId) {
        ProfileActivatedEvent ev = ProfileActivatedEvent.builder()
                .userId(userId)
                .adminId(null)
                .profileType(null)
                .activatedAt(LocalDateTime.now())
                .build();
        publisher.publishProfileActivated(ev, "profile.activated");
    }

    @Override
    public void deactivateProfilesForUserByType(Long userId, String profileType) {
        ProfileSuspendedEvent ev = ProfileSuspendedEvent.builder()
                .suspensionId(UUID.randomUUID().toString())
                .userId(userId)
                .adminId(null)
                .profileType(profileType)
                .reason("suspended_profile_type_by_admin")
                .startAt(LocalDateTime.now())
                .endAt(null)
                .build();
        publisher.publishProfileSuspended(ev, "profile.suspended");
    }

    @Override
    public void activateProfilesForUserByType(Long userId, String profileType) {
        ProfileActivatedEvent ev = ProfileActivatedEvent.builder()
                .userId(userId)
                .adminId(null)
                .profileType(profileType)
                .activatedAt(LocalDateTime.now())
                .build();
        publisher.publishProfileActivated(ev, "profile.activated");
    }
}
