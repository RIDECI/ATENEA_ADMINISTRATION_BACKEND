package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estrategia compuesta que aplica múltiples estrategias en conjunto
 * Requiere que todas las estrategias contenidas sean satisfechas
 *
 * @author RideECI
 * @version 1.0
 */
public class CompositePolicyStrategy implements PolicyStrategy {
    private final List<PolicyStrategy> strategies;

    /**
     * Crea una estrategia compuesta con la lista de estrategias especificada
     *
     * @param strategies Lista de estrategias a aplicar
     */
    public CompositePolicyStrategy(List<PolicyStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Verifica si todas las estrategias contenidas son satisfechas
     *
     * @param policy Política de publicación a evaluar
     * @param at Fecha y hora a verificar
     * @param ctx Contexto de estrategia
     * @return true si TODAS las estrategias son satisfechas, false si alguna falla
     */
    @Override
    public boolean isSatisfied(PublicationPolicy policy, LocalDateTime at, PolicyStrategyContext ctx) {
        for (PolicyStrategy s : strategies) {
            if (!s.isSatisfied(policy, at, ctx)) return false;
        }
        return true;
    }
}
