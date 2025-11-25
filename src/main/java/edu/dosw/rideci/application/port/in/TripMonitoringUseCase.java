package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.infrastructure.controller.dto.Response.TripListItemDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.TripDetailDto;
import edu.dosw.rideci.infrastructure.controller.dto.Response.DashboardResponse;
import java.util.List;

/**
 * Caso de uso para monitoreo de viajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface TripMonitoringUseCase {
    /**
     * Lista viajes con filtros opcionales
     *
     * @param search Término de búsqueda (opcional)
     * @param status Estado del viaje (opcional)
     * @param type Tipo de viaje (opcional)
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de viajes
     */
    List<TripListItemDto> listTrips(String search, String status, String type, int page, int size);

    /**
     * Obtiene viajes activos
     *
     * @return Lista de viajes en progreso
     */
    List<TripListItemDto> getActiveTrips();

    /**
     * Obtiene detalle de un viaje
     *
     * @param tripId ID del viaje
     * @return Detalle del viaje
     */
    TripDetailDto getTripDetail(Long tripId);

    /**
     * Obtiene métricas del dashboard
     *
     * @return Métricas del dashboard
     */
    DashboardResponse getMetrics();
}