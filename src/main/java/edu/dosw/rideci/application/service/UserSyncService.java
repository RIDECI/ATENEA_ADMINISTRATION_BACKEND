package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.port.in.CreateUserUseCase;
import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para sincronización de usuarios en RideECI
 * Realiza operaciones upsert de usuarios recibidos por eventos para mantener
 * la consistencia entre servicios
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserSyncService implements CreateUserUseCase {

    private final UserRepositoryPort userRepo;

    /**
     * Crea o actualiza un usuario en el sistema
     * Asigna fecha de creación si no está presente
     *
     * @param user Usuario a crear o actualizar
     * @return Usuario guardado
     * @throws IllegalArgumentException si el usuario es nulo
     */
    @Override
    public User createUser(User user) {
        if (user == null) throw new IllegalArgumentException("user required");
        return userRepo.save(user);
    }
}
