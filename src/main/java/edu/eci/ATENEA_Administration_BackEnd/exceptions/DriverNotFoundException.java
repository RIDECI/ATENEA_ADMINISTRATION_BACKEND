package edu.eci.ATENEA_Administration_BackEnd.exceptions;

/**
 * Excepción para conductor no encontrado en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(String msg) { super(msg); }
}
