package edu.dosw.rideci.application.events;

import java.time.LocalDateTime;
import java.util.List;

import edu.dosw.rideci.domain.model.valueobjects.Location;
import edu.dosw.rideci.domain.model.enums.Status;
import edu.dosw.rideci.domain.model.enums.TravelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Comando para viaje creado en RideECI
 * Representa la creación de un nuevo viaje en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelCreatedEvent {
    private Long travelId;
    private Long organizerId;
    private Long driverId;
    private Integer availableSlots;
    private Double estimatedCost;
    private Status status;
    private Location origin;
    private Location destiny;
    private List<Long> passengersId;
    private TravelType travelType;
    private LocalDateTime departureDateAndTime;
    private String conditions;
}