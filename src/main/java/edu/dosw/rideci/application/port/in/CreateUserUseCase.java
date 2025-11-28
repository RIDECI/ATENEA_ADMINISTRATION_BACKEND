package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.User;


public interface CreateUserUseCase {
    User createUser(User user);
}
