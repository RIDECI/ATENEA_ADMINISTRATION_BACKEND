package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.port.in.CreateUserUseCase;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio que realiza upsert del usuario recibido por eventos.
 */
@Service
@RequiredArgsConstructor
public class UserSyncService implements CreateUserUseCase {

    private final UserRepositoryPort userRepo;

    @Override
    public User createUser(User user) {
        if (user == null) throw new IllegalArgumentException("user required");
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDateTime.now());
        return userRepo.save(user);
    }
}
