package edu.dosw.rideci.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo de dominio para acciones de administrador en RideECI
 * Registra las operaciones realizadas por administradores en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAction {
    private String id;
    private Long adminId;
    private String action;
    private String targetType;
    private String targetId;
    private String details;
    private LocalDateTime at;
}
