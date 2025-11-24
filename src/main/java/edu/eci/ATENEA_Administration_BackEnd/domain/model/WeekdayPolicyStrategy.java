package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Estrategia para validación de días de la semana
 * Verifica que el día de la semana actual esté en la lista de días permitidos
 *
 * @author RideECI
 * @version 1.0
 */
public class WeekdayPolicyStrategy implements PolicyStrategy {

    /**
     * Verifica si el día de la semana actual está permitido por la política
     *
     * @param policy Política de publicación a evaluar
     * @param at Fecha y hora a verificar
     * @param ctx Contexto de estrategia
     * @return true si no hay días definidos, o si el día actual está en la lista permitida
     */
    @Override
    public boolean isSatisfied(PublicationPolicy policy, LocalDateTime at, PolicyStrategyContext ctx) {
        List<DayOfWeek> allowed = policy.getAllowedDays();
        if (allowed == null || allowed.isEmpty()) return true;
        DayOfWeek d = at.getDayOfWeek();
        return allowed.contains(d);
    }
}
