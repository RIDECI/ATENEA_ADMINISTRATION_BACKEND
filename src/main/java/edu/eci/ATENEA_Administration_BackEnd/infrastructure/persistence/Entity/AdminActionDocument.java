package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Entidad para acciones de administrador en MongoDB
 *
 * @author RideECI
 * @version 1.0
 */
@Document(collection = "admin_actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminActionDocument {

    @Id
    private String id;

    private Long adminId;
    private String action;
    private String targetType;
    private String targetId;
    private String details;
    private LocalDateTime at;
}
