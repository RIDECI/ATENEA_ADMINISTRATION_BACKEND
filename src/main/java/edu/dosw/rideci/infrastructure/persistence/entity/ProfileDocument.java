package edu.dosw.rideci.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import edu.dosw.rideci.domain.model.enums.IdentificationType;
import java.time.LocalDateTime;
import java.util.List;

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

    @Indexed
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileType;
    private String state;
    private List<String> vehicles;
    private Double reputation;
    private List<String> badges;
    private String identificationNumber;
    private IdentificationType identificationType;
    private String address;
    private String profilePictureUrl;
    private LocalDateTime birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}