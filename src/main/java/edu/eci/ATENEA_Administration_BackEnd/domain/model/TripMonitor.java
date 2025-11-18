package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "trip_monitors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripMonitor {

    @Id
    private String monitorId;

    private String tripId;
    private String driverId;
    private int passengerCount;
    private LocalDateTime startTime;
    private String status;
    private String currentLocation;
    private List<String> alerts;
}