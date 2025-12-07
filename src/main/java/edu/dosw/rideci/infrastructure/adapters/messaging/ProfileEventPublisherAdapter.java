package edu.dosw.rideci.infrastructure.adapters.messaging;

import edu.dosw.rideci.application.events.ProfileCommandEvent;
import edu.dosw.rideci.application.port.out.ProfileClientPort;
import edu.dosw.rideci.application.port.out.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

    private final EventPublisher eventPublisher;

    /**
     * Publica evento para desactivar perfiles de un usuario
     *
     * @param userId ID del usuario
     */
    @Override
    public void deactivateProfilesForUser(Long userId) {
        ProfileCommandEvent ev = ProfileCommandEvent.builder()
                .userId(userId)
                .command("DEACTIVATE")
                .occurredAt(LocalDateTime.now())
                .build();
        eventPublisher.publish(ev, "profile.command.deactivate");
    }

    /**
     * Publica evento para activar perfiles de un usuario
     *
     * @param userId ID del usuario
     */
    @Override
    public void activateProfilesForUser(Long userId) {
        ProfileCommandEvent ev = ProfileCommandEvent.builder()
                .userId(userId)
                .command("ACTIVATE")
                .occurredAt(LocalDateTime.now())
                .build();
        eventPublisher.publish(ev, "profile.command.activate");
    }

    @Override
    public void deactivateProfilesForUserByType(Long userId, String profileType) {
        ProfileCommandEvent ev = ProfileCommandEvent.builder()
                .userId(userId)
                .command("DEACTIVATE")
                .scope("PROFILE_TYPE")
                .profileType(profileType)
                .reason("suspended_profile_type_by_admin")
                .occurredAt(LocalDateTime.now())
                .build();
        eventPublisher.publish(ev, "profile.command.deactivate");
    }

    @Override
    public void activateProfilesForUserByType(Long userId, String profileType) {
        ProfileCommandEvent ev = ProfileCommandEvent.builder()
                .userId(userId)
                .command("ACTIVATE")
                .scope("PROFILE_TYPE")
                .profileType(profileType)
                .occurredAt(LocalDateTime.now())
                .build();
        eventPublisher.publish(ev, "profile.command.activate");
    }
}
