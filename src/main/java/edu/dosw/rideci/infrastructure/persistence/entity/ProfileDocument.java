package edu.dosw.rideci.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

/**
 * Documento de perfil para persistencia en MongoDB
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "profiles")
public class ProfileDocument {

    @Id
    private String id;

    /**
     * ID del usuario (índice para búsquedas rápidas)
     */
    @Indexed
    private Long userId;

    private String name;
    private String email;
    private String phoneNumber;
    private String profileType;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}