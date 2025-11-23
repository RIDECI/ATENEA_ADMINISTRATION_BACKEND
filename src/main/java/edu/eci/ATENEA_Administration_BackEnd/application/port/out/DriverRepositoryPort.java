package edu.eci.ATENEA_Administration_BackEnd.application.port.out;

import java.util.List;
import java.util.Optional;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.Driver;

/**
 * Puerto de salida para operaciones de repositorio de conductores en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface DriverRepositoryPort {
    /**
     * Guarda un conductor
     *
     * @param d Conductor a guardar
     * @return Conductor guardado
     */
    Driver save(Driver d);
    /**
     * Busca un conductor por ID
     *
     * @param id ID del conductor
     * @return Conductor encontrado (opcional)
     */
    Optional<Driver> findById(Long id);
    /**
     * Busca conductores por estado
     *
     * @param status Estado del conductor
     * @return Lista de conductores
     */
    List<Driver> findByStatus(String status);
    /**
     * Busca conductores por nombre
     *
     * @param q Término de búsqueda
     * @return Lista de conductores
     */
    List<Driver> searchByName(String q);
    /**
     * Obtiene todos los conductores paginados
     *
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de conductores paginados
     */
    List<Driver> findAllPaged(int page, int size);
    /**
     * Obtiene todos los conductores
     *
     * @return Lista de todos los conductores
     */
    List<Driver> findAll();
}
