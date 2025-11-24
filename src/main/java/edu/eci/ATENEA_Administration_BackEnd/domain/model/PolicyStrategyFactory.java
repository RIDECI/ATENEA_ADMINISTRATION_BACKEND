package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica para crear estrategias compuestas basadas en una política de publicación
 *
 * @author RideECI
 * @version 1.0
 */
public class PolicyStrategyFactory {

    /**
     * Crea una estrategia compuesta basada en los criterios definidos en la política
     *
     * @param policy Política de publicación a analizar
     * @return Estrategia compuesta con las validaciones requeridas
     */
    public static PolicyStrategy of(PublicationPolicy policy) {
        List<PolicyStrategy> list = new ArrayList<>();
        list.add(new TimeRangePolicyStrategy());

        if (policy.getAllowedDays() != null && !policy.getAllowedDays().isEmpty()) {
            list.add(new WeekdayPolicyStrategy());
        }

        if (policy.getAllowedRoles() != null && !policy.getAllowedRoles().isEmpty()) {
            list.add(new RolePolicyStrategy());
        }
        return new CompositePolicyStrategy(list);
    }
}
