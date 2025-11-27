package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.ExportedReport;
import edu.dosw.rideci.domain.model.SecurityReport;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Casos de uso para gestión y exportación de reportes de seguridad en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface ReportUseCase {
    /**
     * Crea un nuevo reporte de seguridad
     *
     * @param r Reporte de seguridad a crear
     * @return Reporte de seguridad creado
     */
    SecurityReport createReport(SecurityReport r);

    /**
     * Lista reportes de seguridad con filtros opcionales
     *
     * @param type Tipo de reporte (opcional)
     * @param from Fecha de inicio (opcional)
     * @param to Fecha de fin (opcional)
     * @return Lista de reportes de seguridad
     */
    List<SecurityReport> listReports(String type, LocalDateTime from, LocalDateTime to);

    /**
     * Exporta reportes a formato CSV
     *
     * @param reports Lista de reportes a exportar
     * @return String con contenido CSV
     */
    String exportReportsToCsv(List<SecurityReport> reports);

    /**
     * Exporta reportes a formato XLSX (Excel)
     *
     * @param reports Lista de reportes a exportar
     * @return Bytes del archivo Excel
     */
    byte[] exportReportsToXlsx(List<SecurityReport> reports);

    /**
     * Exporta reportes a formato PDF
     *
     * @param reports Lista de reportes a exportar
     * @return Bytes del archivo PDF
     */
    byte[] exportReportsToPdf(List<SecurityReport> reports);

    /**
     * Exporta reportes al formato especificado
     *
     * @param format Formato de exportación (pdf, csv, xlsx)
     * @param reports Lista de reportes a exportar
     * @return ExportedReport con contenido y metadatos
     */
    ExportedReport exportReportsAs(String format, List<SecurityReport> reports);
}