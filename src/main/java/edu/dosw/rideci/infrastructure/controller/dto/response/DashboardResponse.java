package edu.dosw.rideci.infrastructure.controller.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO para respuesta del dashboard en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private int tripsToday;
    private int tripsInProgress;
    private double income;
    private double co2Reduced;
    private int openSecurityReports;
    private String changeSinceLastPeriod;
    private LocalDateTime lastUpdate;
}
