package edu.dosw.rideci.application.port.out;

import edu.dosw.rideci.domain.model.SecurityReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de repositorio de reportes de seguridad en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface SecurityReportRepositoryPort {
    /**
     * Guarda un reporte de seguridad
     *
     * @param r Reporte de seguridad a guardar
     * @return Reporte de seguridad guardado
     */
    SecurityReport save(SecurityReport r);
    /**
     * Busca un reporte de seguridad por ID
     *
     * @param id ID del reporte
     * @return Reporte de seguridad encontrado opcional
     */
    Optional<SecurityReport> findById(String id);

    /**
     * Obtiene todos los reportes de seguridad
     *
     * @return Lista de todos los reportes de seguridad
     */
    List<SecurityReport> findAll();

    /**
     * Verifica si existe un reporte por ID relacionado y tipo
     *
     * @param relatedId ID relacionado
     * @param type Tipo de reporte
     * @return True si existe, false en caso contrario
     */
    boolean existsByRelatedIdAndType(String relatedId, String type);

    /**
     * Busca reportes de seguridad por tipo
     *
     * @param type Tipo de reporte
     * @return Lista de reportes de seguridad
     */
    List<SecurityReport> findByType(String type);

    /**
     * Busca reportes de seguridad por período de tiempo
     *
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Lista de reportes de seguridad
     */
    List<SecurityReport> findByPeriod(LocalDateTime from, LocalDateTime to);
}
