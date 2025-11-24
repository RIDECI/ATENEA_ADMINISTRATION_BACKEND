package edu.eci.ATENEA_Administration_BackEnd.domain.model;

import java.time.LocalDateTime;

/**
 * Interfaz para estrategias de validación de políticas de publicación
 *
 * @author RideECI
 * @version 1.0
 */
public interface PolicyStrategy {
    boolean isSatisfied(PublicationPolicy policy, LocalDateTime at, PolicyStrategyContext ctx);
}
