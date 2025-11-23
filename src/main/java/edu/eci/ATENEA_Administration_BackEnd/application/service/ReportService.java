package edu.eci.ATENEA_Administration_BackEnd.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import edu.eci.ATENEA_Administration_BackEnd.application.port.out.SecurityReportRepositoryPort;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.SecurityReport;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Servicio para gestión de reportes de seguridad en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final SecurityReportRepositoryPort reportRepo;

    /**
     * Crea un nuevo reporte de seguridad
     *
     * @param r Reporte de seguridad a crear
     * @return Reporte de seguridad creado
     */
    public SecurityReport createReport(SecurityReport r) {
        r.setCreatedAt(LocalDateTime.now());
        r.setStatus(r.getStatus() == null ? "OPEN" : r.getStatus());
        return reportRepo.save(r);
    }

    /**
     * Lista reportes de seguridad con filtros opcionales
     *
     * @param type Tipo de reporte (opcional)
     * @param from Fecha de inicio (opcional)
     * @param to Fecha de fin (opcional)
     * @return Lista de reportes de seguridad
     */
    public List<SecurityReport> listReports(String type, LocalDateTime from, LocalDateTime to) {
        if (type != null && !type.isBlank()) return reportRepo.findByType(type);
        if (from != null && to != null) return reportRepo.findByPeriod(from, to);
        return reportRepo.findAll();
    }


    /**
     * Exporta reportes a formato CSV
     *
     * @param reports Lista de reportes a exportar
     * @return String con contenido CSV
     */
    public String exportReportsToCsv(List<SecurityReport> reports) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,title,type,createdBy,occurredAt,createdAt,status,description\n");
        for (SecurityReport r : reports) {
            sb.append(r.getId()).append(",")
                    .append(escapeCsv(r.getTitle())).append(",")
                    .append(r.getType()).append(",")
                    .append(r.getCreatedBy()).append(",")
                    .append(r.getOccurredAt()).append(",")
                    .append(r.getCreatedAt()).append(",")
                    .append(r.getStatus()).append(",")
                    .append(escapeCsv(r.getDescription())).append("\n");
        }
        return sb.toString();
    }

    /**
     * Escapa texto para formato CSV
     *
     * @param s Texto a escapar
     * @return Texto escapado
     */
    private String escapeCsv(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    /**
     * Exporta reportes a bytes para Excel
     *
     * @param reports Lista de reportes a exportar
     * @return Bytes del archivo Excel
     */
    public byte[] exportReportsToExcelBytes(List<SecurityReport> reports) {
        return exportReportsToCsv(reports).getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
