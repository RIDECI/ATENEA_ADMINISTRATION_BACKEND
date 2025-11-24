package edu.dosw.rideci.domain.model;

import edu.dosw.rideci.domain.model.Enum.TripStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Modelo de dominio para monitoreo de viajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripMonitor {
    private Long tripId;
    private Long driverId;
    private String driverName;
    private List<Long> passengerIds;
    private List<String> passengerNames;
    private TripStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double estimatedCost;
    private double co2Saved;
    private String origin;
    private String destination;
    private String travelType;
}
