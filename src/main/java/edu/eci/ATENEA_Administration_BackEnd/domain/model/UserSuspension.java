package edu.eci.ATENEA_Administration_BackEnd.domain.model;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.Enum.SuspensionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "user_suspensions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSuspension {

    @Id
    private String suspensionId;

    private String userId;
    private String adminId;
    private String reason;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SuspensionType suspensionType;
    private boolean isActive;
}