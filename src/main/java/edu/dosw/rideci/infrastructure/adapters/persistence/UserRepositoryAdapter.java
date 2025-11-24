package edu.dosw.rideci.infrastructure.adapters.persistence;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import edu.dosw.rideci.application.port.out.UserRepositoryPort;
import edu.dosw.rideci.infrastructure.persistence.Repository.UserMongoRepository;
import edu.dosw.rideci.infrastructure.persistence.Repository.mapper.UserDocumentMapper;
import edu.dosw.rideci.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para usuarios en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserMongoRepository repo;
    private final UserDocumentMapper mapper;

    /**
     * Obtiene todos los usuarios paginados
     *
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de usuarios paginados
     */
    @Override
    public List<User> findAllPaged(int page, int size) {
        return repo.findAll(org.springframework.data.domain.PageRequest.of(page, size))
                .getContent().stream().map(mapper::toDomain).toList();
    }

    /**
     * Busca usuarios por nombre
     *
     * @param search Término de búsqueda
     * @return Lista de usuarios
     */
    @Override
    public List<User> searchByName(String search) {
        return mapper.toListDomain(repo.findByNameContainingIgnoreCase(search));
    }

    /**
     * Busca usuarios por estado
     *
     * @param status Estado del usuario
     * @return Lista de usuarios
     */
    @Override
    public List<User> findByStatus(String status) {
        return mapper.toListDomain(repo.findByState(status));
    }

    /**
     * Busca un usuario por ID
     *
     * @param id ID del usuario
     * @return Usuario encontrado (opcional)
     */
    @Override
    public Optional<User> findById(Long id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    /**
     * Guarda un usuario
     *
     * @param u Usuario a guardar
     * @return Usuario guardado
     */
    @Override
    public User save(User u) {
        return mapper.toDomain(repo.save(mapper.toDocument(u)));
    }

    /**
     * Actualiza el estado de un usuario
     *
     * @param userId ID del usuario
     * @param newStatus Nuevo estado
     * @return Usuario actualizado
     * @throws java.util.NoSuchElementException Si el usuario no existe
     */
    @Override
    public User updateStatus(Long userId, String newStatus) {
        var doc = repo.findById(userId).orElseThrow(() -> new java.util.NoSuchElementException("User not found"));
        doc.setState(newStatus);
        return mapper.toDomain(repo.save(doc));
    }

    /**
     * Obtiene todos los usuarios
     *
     * @return Lista de todos los usuarios
     */
    @Override
    public List<User> findAll() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }
}
