package edu.dosw.rideci.application.port.in;

import java.util.List;
import edu.dosw.rideci.domain.model.Driver;

/**
 * Caso de uso para obtención de conductores en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface GetDriversUseCase {
    /**
     * Lista conductores con filtros opcionales
     *
     * @param status Estado del conductor (opcional)
     * @param search Término de búsqueda (opcional)
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de conductores
     */
    List<Driver> listDrivers(String status, String search, int page, int size);
    /**
     * Obtiene un conductor por ID
     *
     * @param id ID del conductor
     * @return Conductor encontrado
     */
    Driver getDriver(Long id);
}
