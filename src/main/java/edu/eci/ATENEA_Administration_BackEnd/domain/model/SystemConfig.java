package edu.eci.ATENEA_Administration_BackEnd.domain.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "system_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig {

    @Id
    private String configId;

    private String allowedTripHours;
    private int maxTripDuration;
    private boolean driverVerificationRequired;
    private String securityLevel;
    private int autoSuspendThreshold;
    private int maxPassengersPerTrip;
    private LocalDateTime lastModified;
}
