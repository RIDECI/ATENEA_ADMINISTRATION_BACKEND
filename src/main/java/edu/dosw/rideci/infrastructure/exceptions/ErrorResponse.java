package edu.dosw.rideci.infrastructure.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Estructura estáadar utilizada para devolver errores al cliente.
 * Incluye detalles utiles para debugging y manejo de errores.
 *
 *  @author RideECI
 *  @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;
}