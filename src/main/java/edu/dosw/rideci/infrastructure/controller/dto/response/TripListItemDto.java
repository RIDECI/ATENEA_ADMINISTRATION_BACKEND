package edu.dosw.rideci.infrastructure.controller.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para item de lista de viajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripListItemDto {
    private Long tripId;
    private Long driverId;
    private String driverName;
    private List<Long> passengerIds;
    private String status;
    private LocalDateTime startTime;
    private double estimatedCost;
    private String origin;
    private String destination;
}
