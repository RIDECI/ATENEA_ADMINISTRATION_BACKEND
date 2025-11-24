package edu.dosw.rideci.application.port.out;

import edu.dosw.rideci.domain.model.Enum.TripStatus;
import edu.dosw.rideci.domain.model.TripMonitor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto de salida para operaciones de repositorio de viajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface TripRepositoryPort {

    /**
     * Cuenta viajes por estado
     *
     * @param status Estado del viaje
     * @return Cantidad de viajes
     */
    long countByStatus(TripStatus status);

    /**
     * Cuenta viajes por rango de tiempo
     *
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Cantidad de viajes
     */
    long countByStartTimeBetween(LocalDateTime from, LocalDateTime to);

    /**
     * Busca viajes por estado
     *
     * @param status Estado del viaje
     * @return Lista de viajes
     */
    List<TripMonitor> findByStatus(TripStatus status);

    /**
     * Obtiene todos los viajes paginados
     *
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de viajes paginados
     */
    List<TripMonitor> findAllPaged(int page, int size);

    /**
     * Suma los ingresos en un rango de tiempo
     *
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Suma de ingresos
     */
    double sumIncomeBetween(LocalDateTime from, LocalDateTime to);

    /**
     * Suma el CO2 ahorrado en un rango de tiempo
     *
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Suma de CO2 ahorrado
     */
    double sumCo2Between(LocalDateTime from, LocalDateTime to);

    /**
     * Obtiene un viaje por ID
     *
     * @param tripId ID del viaje
     * @return Viaje encontrado
     */
    TripMonitor getTripById(Long tripId);
}
