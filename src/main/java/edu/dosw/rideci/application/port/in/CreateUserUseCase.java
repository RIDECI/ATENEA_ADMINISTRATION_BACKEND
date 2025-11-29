package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.User;


/**
 * Caso de uso para manejar un usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface CreateUserUseCase {
    User createUser(User user);
}
