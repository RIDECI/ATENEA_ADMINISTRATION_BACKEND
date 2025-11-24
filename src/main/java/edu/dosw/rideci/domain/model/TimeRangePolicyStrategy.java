package edu.dosw.rideci.domain.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Estrategia para validación de rango de tiempo
 * Verifica que la hora actual esté dentro del rango definido por la política
 * Maneja rangos que cruzan la medianoche (ej: 22:00 - 06:00)
 *
 * @author RideECI
 * @version 1.0
 */
public class TimeRangePolicyStrategy implements PolicyStrategy {

    /**
     * Verifica si la hora actual está dentro del rango de tiempo de la política
     *
     * @param policy Política de publicación a evaluar
     * @param at Fecha y hora a verificar
     * @param ctx Contexto de estrategia (no utilizado en esta estrategia)
     * @return true si no hay rangos definidos, o si la hora está dentro del rango
     */
    @Override
    public boolean isSatisfied(PublicationPolicy policy, LocalDateTime at, PolicyStrategyContext ctx) {
        if (policy == null) return false;
        LocalTime start = policy.getStartTime();
        LocalTime end = policy.getEndTime();
        if (start == null || end == null) return true;
        LocalTime t = at.toLocalTime();
        if (start.equals(end)) return true;
        if (!start.isAfter(end)) {
            return (!t.isBefore(start)) && (!t.isAfter(end));
        } else {
            return (!t.isBefore(start)) || (!t.isAfter(end));
        }
    }
}
