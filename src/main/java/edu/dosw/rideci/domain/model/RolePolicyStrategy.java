package edu.dosw.rideci.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estrategia para validación basada en roles de usuario
 * Verifica que el rol del usuario esté incluido en los roles permitidos por la política
 *
 * @author RideECI
 * @version 1.0
 */
public class RolePolicyStrategy implements PolicyStrategy {

    /**
     * Verifica si el rol del usuario está permitido por la política
     *
     * @param policy Política de publicación a evaluar
     * @param at Fecha y hora a verificar
     * @param ctx Contexto que contiene el rol del usuario
     * @return true si la política no tiene roles definidos, o si el rol del usuario está en la lista permitida
     */
    @Override
    public boolean isSatisfied(PublicationPolicy policy, LocalDateTime at, PolicyStrategyContext ctx) {
        List<String> allowedRoles = policy.getAllowedRoles();
        if (allowedRoles == null || allowedRoles.isEmpty()) return true;
        if (ctx == null || ctx.getUserRole() == null) return false;
        return allowedRoles.contains(ctx.getUserRole());
    }
}
