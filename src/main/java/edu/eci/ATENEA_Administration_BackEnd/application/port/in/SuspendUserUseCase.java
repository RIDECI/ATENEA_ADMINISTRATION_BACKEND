package edu.eci.ATENEA_Administration_BackEnd.application.port.in;

import edu.eci.ATENEA_Administration_BackEnd.infrastructure.controller.dto.Request.SuspendUserRequestDto;

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
