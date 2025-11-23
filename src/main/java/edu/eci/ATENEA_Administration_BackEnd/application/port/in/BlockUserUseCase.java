package edu.eci.ATENEA_Administration_BackEnd.application.port.in;

/**
 * Caso de uso para bloqueo de usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface BlockUserUseCase {
    /**
     * Bloquea un usuario en el sistema
     *
     * @param userId ID del usuario a bloquear
     * @param adminId ID del administrador que realiza la acción
     * @param reason Motivo del bloqueo
     */
    void blockUser(Long userId, Long adminId, String reason);
}
