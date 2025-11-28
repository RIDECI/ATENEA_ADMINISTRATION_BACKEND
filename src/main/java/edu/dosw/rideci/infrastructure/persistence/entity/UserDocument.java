package edu.dosw.rideci.infrastructure.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Documento de usuario para persistencia en MongoDB
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class UserDocument {

    @Id
    private Long id;

    private String name;
    private String email;
    private String role;
    private String state;
    private double reputation;
    private LocalDateTime lastLogin;
    private String phoneNumber;
    private LocalDateTime createdAt;
}
