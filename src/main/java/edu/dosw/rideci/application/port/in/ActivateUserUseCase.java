package edu.dosw.rideci.application.port.in;

/**
 * Caso de uso para activación de usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface ActivateUserUseCase {
    /**
     * Activa un usuario en el sistema
     *
     * @param userId ID del usuario a activar
     * @param adminId ID del administrador que realiza la acción
     * @param profileType optional -> si no es null, solo reactiva profiles de ese tipo
     */
    void activateUser(Long userId, Long adminId, String profileType);
}

