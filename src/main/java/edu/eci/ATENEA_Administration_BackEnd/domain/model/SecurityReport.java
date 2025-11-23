package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Modelo de dominio para reporte de seguridad en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityReport {
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
