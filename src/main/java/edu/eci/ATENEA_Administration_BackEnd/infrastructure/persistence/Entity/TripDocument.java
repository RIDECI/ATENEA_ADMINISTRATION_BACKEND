package edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity;

import edu.eci.ATENEA_Administration_BackEnd.domain.model.Enum.TripStatus;
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
