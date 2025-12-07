package edu.dosw.rideci.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de comando para perfiles en RideECI
 * Se utiliza para activar o desactivar perfiles de usuario en el sistema
 *
 * @author RideECI
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileCommandEvent {
    private Long userId;            // usuario objetivo
    private String command;         // activar o desactivar
    private String scope;           // todos o solo un perfil
    private String profileType;
    private String reason;
    private LocalDateTime occurredAt;
}