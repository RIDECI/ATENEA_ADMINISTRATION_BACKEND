package edu.dosw.rideci.domain.model.valueobjects;

import lombok.*;

/**
 * Value Object para ubicación geográfica en RideECI
 * Representa coordenadas y dirección de una ubicación
 *
 * @author RideECI
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    private double longitude;
    private double latitude;
    private String direction;
}
