package edu.dosw.rideci.application.events.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.dosw.rideci.domain.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class TravelCompletedCommand {
    @JsonProperty("id")
    private Long travelId;
    private Status state;
}
