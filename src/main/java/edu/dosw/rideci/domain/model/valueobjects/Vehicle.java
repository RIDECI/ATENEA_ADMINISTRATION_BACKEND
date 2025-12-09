package edu.dosw.rideci.domain.model.valueobjects;

import edu.dosw.rideci.domain.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Value Object para vehículos en RideECI
 * Representa información de vehículos registrados por conductores
 *
 * @author RideECI
 * @version 1.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

    private String vehiclePlate;
    private String color;
    private VehicleType vehicleType;
    private String vehicleModel;
    private String vehiclePhoto;

}