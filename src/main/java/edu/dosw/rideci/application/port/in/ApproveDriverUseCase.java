package edu.dosw.rideci.application.port.in;

/**
 * Caso de uso para aprobación y rechazo de conductores en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface ApproveDriverUseCase {
    /**
     * Aprueba un conductor en el sistema
     *
     * @param driverId ID del conductor a aprobar
     * @param adminId ID del administrador que realiza la acción
     */
    void approveDriver(Long driverId, Long adminId);
    /**
     * Rechaza un conductor en el sistema
     *
     * @param driverId ID del conductor a rechazar
     * @param adminId ID del administrador que realiza la acción
     * @param reason Motivo del rechazo
     */
    void rejectDriver(Long driverId, Long adminId, String reason);
}
