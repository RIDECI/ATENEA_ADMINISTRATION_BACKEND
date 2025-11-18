package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "admin_dashboards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboard {

    @Id
    private String dashboardId;

    private int activeTrips;
    private int pendingValidations;
    private int securityReports;
    private int suspendedUsers;
    private String systemHealth;
    private LocalDateTime lastUpdate;
}