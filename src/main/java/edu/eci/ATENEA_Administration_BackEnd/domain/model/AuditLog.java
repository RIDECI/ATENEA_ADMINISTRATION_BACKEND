package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    private String logId;

    private String adminId;
    private String action;
    private String targetUserId;
    private LocalDateTime timestamp;
    private String details;
    private String ipAddress;
}