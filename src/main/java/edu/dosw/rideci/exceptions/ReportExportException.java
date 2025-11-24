package edu.dosw.rideci.exceptions;

/**
 * Excepción específica para errores durante la exportación de reportes.
 *
 * @author RideECI
 * @version 1.0
 */
public class ReportExportException extends RuntimeException {

    public ReportExportException(String message, Throwable cause) {
        super(message, cause);
    }
}