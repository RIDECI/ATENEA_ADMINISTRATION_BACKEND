package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.infrastructure.controller.dto.request.SuspendUserRequestDto;

/**
 * Caso de uso para suspensión de usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface SuspendUserUseCase {
    /**
     * Suspende un usuario en el sistema
     *
     * @param userId ID del usuario a suspender
     * @param req Datos de la suspensión
     */
    void suspendUser(Long userId, SuspendUserRequestDto req);
}
