package edu.eci.ATENEA_Administration_BackEnd.application.port.in;

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
     */
    void activateUser(Long userId, Long adminId);
}
