package edu.dosw.rideci.domain.model.valueobjects;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Value Object para reputación de usuario en RideECI
 * Calcula y almacena la reputación basada en calificaciones ponderadas
 *
 * @author RideECI
 * @version 1.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reputation {

    private HashMap<Integer, Double> weightedScores;
    private double average;
    private int totalRatings;


}