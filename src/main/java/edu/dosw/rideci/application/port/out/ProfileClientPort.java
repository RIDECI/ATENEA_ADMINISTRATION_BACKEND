package edu.dosw.rideci.application.port.out;

/**
 * Puerto de salida para cliente de perfiles en RideECI
 * Proporciona operaciones para activar/desactivar perfiles de usuario
 *
 * @author RideECI
 * @version 1.0
 */
public interface ProfileClientPort {
    /**
     * Desactiva perfiles de un usuario
     *
     * @param userId ID del usuario
     */
    void deactivateProfilesForUser(Long userId);

    /**
     * Activa  perfiles de un usuario
     *
     * @param userId ID del usuario
     */
    void activateProfilesForUser(Long userId);

    /**
     * Desactiva los perfiles de un usuario por tipo específico
     * Permite desactivar selectivamente perfiles según su tipo "DRIVER", "PASSENGER", "COMPANION"
     *
     * @param userId ID del usuario
     * @param profileType Tipo de perfil a desactivar
     */
    void deactivateProfilesForUserByType(Long userId, String profileType);

    /**
     * Activa los perfiles de un usuario por tipo específico
     * Permite activar selectivamente perfiles según su tipo "DRIVER", "PASSENGER", "COMPANION"
     *
     * @param userId ID del usuario
     * @param profileType Tipo de perfil a activar
     */
    void activateProfilesForUserByType(Long userId, String profileType);
}
