package edu.dosw.rideci.application.exceptions;


/**
 * Excepción para usuario no encontrado en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String msg) { super(msg); }
}
