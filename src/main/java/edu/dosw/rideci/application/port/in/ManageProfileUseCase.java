package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.application.events.ProfileEvent;
import edu.dosw.rideci.domain.model.Profile;

/**
 * Caso de uso para gestión de perfiles en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface ManageProfileUseCase {
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
     * Desactiva un perfil de usuario
     *
     * @param userId ID del usuario
     * @param adminId ID del administrador que realiza la acción
     * @param profileType Tipo de perfil a desactivar (opcional)
     */
    void deactivateProfile(Long userId, Long adminId, String profileType);
}