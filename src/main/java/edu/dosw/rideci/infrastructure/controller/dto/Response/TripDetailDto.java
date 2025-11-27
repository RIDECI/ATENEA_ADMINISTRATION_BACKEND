package edu.dosw.rideci.infrastructure.controller.dto.Response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para detalle de viaje en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDetailDto {
    private Long tripId;
    private Long driverId;
    private String driverName;
    private List<Long> passengerIds;
    private List<String> passengerNames;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double estimatedCost;
    private double co2Saved;
    private String origin;
    private String destination;
    private String travelType;
}
