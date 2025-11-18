package edu.eci.ATENEA_Administration_BackEnd.domain.model;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.Enum.Permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "administrators")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrator {

    @Id
    private String adminId;

    private String name;
    private String email;
    private String department;
    private List<Permission> permissions;
    private LocalDateTime lastLogin;
}
