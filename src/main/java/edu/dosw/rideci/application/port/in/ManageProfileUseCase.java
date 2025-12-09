package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.application.events.ProfileEvent;
import edu.dosw.rideci.domain.model.Profile;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para gestión de perfiles en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface ManageProfileUseCase {

    Optional<Profile> getProfileDetails(Long userId);
    List<Profile> listProfiles(String search, String profileType, int page, int size);

    /**
     * Crea o actualiza un perfil desde un evento
     *
     * @param ev Evento de perfil con datos a upsert
     * @return Perfil creado o actualizado
     */
    Profile upsertFromEvent(ProfileEvent ev);

    /**
     * Activa un perfil de usuario
     *
     * @param userId ID del usuario
     * @param adminId ID del administrador que realiza la acción
     * @param profileType Tipo de perfil a activar (opcional)
     */
    void activateProfile(Long userId, Long adminId, String profileType);

    /**
     * Suspende un perfil de usuario
     *
     * @param userId ID del usuario
     * @param adminId ID del administrador que realiza la acción
     * @param profileType Tipo de perfil a desactivar (opcional)
     */
    void suspendProfile(Long userId, Long adminId, String profileType, String reason, String startAt, String endAt);

    /**
     * Desactiva perfil
     * @param userId
     * @param adminId
     * @param profileType
     */
    void deactivateProfile(Long userId, Long adminId, String profileType);
}