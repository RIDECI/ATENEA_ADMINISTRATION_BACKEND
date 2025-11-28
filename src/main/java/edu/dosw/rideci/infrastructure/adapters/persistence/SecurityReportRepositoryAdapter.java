package edu.dosw.rideci.infrastructure.adapters.persistence;

import edu.dosw.rideci.application.port.out.SecurityReportRepositoryPort;
import edu.dosw.rideci.domain.model.SecurityReport;
import edu.dosw.rideci.infrastructure.persistence.entity.SecurityReportDocument;
import edu.dosw.rideci.infrastructure.persistence.repository.SecurityReportMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adaptador de persistencia para reportes de seguridad en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class SecurityReportRepositoryAdapter implements SecurityReportRepositoryPort {

    private final SecurityReportMongoRepository repo;

    /**
     * Guarda un reporte de seguridad
     *
     * @param r Reporte de seguridad a guardar
     * @return Reporte de seguridad guardado
     */
    @Override
    public SecurityReport save(SecurityReport r) {
        if (r.getCreatedAt() == null) r.setCreatedAt(LocalDateTime.now());
        if (r.getStatus() == null) r.setStatus("OPEN");
        SecurityReportDocument doc = toDocument(r);
        SecurityReportDocument saved = repo.save(doc);
        return toDomain(saved);
    }

    /**
     * Busca un reporte de seguridad por ID
     *
     * @param id ID del reporte
     * @return Reporte de seguridad encontrado (opcional)
     */
    @Override
    public java.util.Optional<SecurityReport> findById(String id) {
        return repo.findById(id).map(this::toDomain);
    }

    /**
     * Obtiene todos los reportes de seguridad
     *
     * @return Lista de todos los reportes de seguridad
     */
    @Override
    public List<SecurityReport> findAll() {
        return repo.findAll().stream().map(this::toDomain).toList();
    }

    /**
     * Verifica si existe un reporte por ID relacionado y tipo
     *
     * @param relatedId ID relacionado
     * @param type Tipo de reporte
     * @return True si existe, false en caso contrario
     */
    @Override
    public boolean existsByRelatedIdAndType(String relatedId, String type) {
        return repo.existsByRelatedIdAndType(relatedId, type);
    }

    /**
     * Busca reportes de seguridad por tipo
     *
     * @param type Tipo de reporte
     * @return Lista de reportes de seguridad
     */
    @Override
    public List<SecurityReport> findByType(String type) {
        return repo.findByType(type).stream().map(this::toDomain).toList();
    }

    /**
     * Busca reportes de seguridad por período de tiempo
     *
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Lista de reportes de seguridad
     */
    @Override
    public List<SecurityReport> findByPeriod(LocalDateTime from, LocalDateTime to) {
        return repo.findByOccurredAtBetween(from, to).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Convierte documento a dominio
     *
     * @param d Documento de seguridad
     * @return Modelo de dominio de seguridad
     */
    private SecurityReport toDomain(SecurityReportDocument d) {
        return SecurityReport.builder()
                .id(d.getId())
                .title(d.getTitle())
                .type(d.getType())
                .createdBy(d.getCreatedBy())
                .description(d.getDescription())
                .occurredAt(d.getOccurredAt())
                .relatedId(d.getRelatedId())
                .severity(d.getSeverity())
                .createdAt(d.getCreatedAt())
                .status(d.getStatus())
                .build();
    }

    /**
     * Convierte dominio a documento
     *
     * @param r Modelo de dominio de seguridad
     * @return Documento de seguridad
     */
    private SecurityReportDocument toDocument(SecurityReport r) {
        return SecurityReportDocument.builder()
                .id(r.getId())
                .title(r.getTitle())
                .type(r.getType())
                .createdBy(r.getCreatedBy())
                .description(r.getDescription())
                .occurredAt(r.getOccurredAt())
                .relatedId(r.getRelatedId())
                .severity(r.getSeverity())
                .createdAt(r.getCreatedAt())
                .status(r.getStatus())
                .build();
    }
}
