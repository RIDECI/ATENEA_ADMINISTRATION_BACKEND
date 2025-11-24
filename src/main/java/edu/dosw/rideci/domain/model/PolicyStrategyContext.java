package edu.dosw.rideci.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contexto para estrategias de validación de políticas
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyStrategyContext {
    private Long userId;
    private String userRole;
}
