package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.List;

/**
 * Entidad para políticas de publicación en MongoDB
 *
 * @author RideECI
 * @version 1.0
 */
@Document(collection = "publication_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicationPolicyDocument {

    @Id
    private String id;

    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean enabled;
    private String description;
    private List<String> allowedDays;
    private List<String> allowedRoles;
}
