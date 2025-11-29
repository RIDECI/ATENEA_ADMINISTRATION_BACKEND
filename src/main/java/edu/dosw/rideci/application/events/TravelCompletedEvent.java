package edu.dosw.rideci.application.events;

import edu.dosw.rideci.domain.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Comando para viaje completado en RideECI
 * Representa la finalización de un viaje en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelCompletedEvent{
    private Long travelId;
    private Long driverId;
    private List<Long> passengerList;
    private Status state;
}
