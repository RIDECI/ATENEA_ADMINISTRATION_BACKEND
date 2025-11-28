package edu.dosw.rideci.infrastructure.persistence.entity;

import edu.dosw.rideci.domain.model.enums.TripStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento de viaje para persistencia en MongoDB
 *
 * @author RideECI
 * @version 1.0
 */
@Document(collection = "trips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDocument {

    @Id
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
