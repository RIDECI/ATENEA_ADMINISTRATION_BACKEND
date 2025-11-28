package edu.dosw.rideci.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Documento de reporte de seguridad para persistencia en MongoDB
 *
 * @author RideECI
 * @version 1.0
 */
@Document(collection = "security_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityReportDocument {

    @Id
    private String id;

    private String title;
    private String type;
    private Long createdBy;
    private String description;
    private LocalDateTime occurredAt;
    private String relatedId;
    private String severity;
    private LocalDateTime createdAt;
    private String status;
}
