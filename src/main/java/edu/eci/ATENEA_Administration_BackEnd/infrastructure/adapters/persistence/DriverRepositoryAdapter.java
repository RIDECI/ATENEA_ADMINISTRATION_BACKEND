package edu.eci.ATENEA_Administration_BackEnd.infrastructure.adapters.persistence;

import edu.eci.ATENEA_Administration_BackEnd.application.port.out.DriverRepositoryPort;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.Driver;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.DriverDocument;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository.DriverMongoRepository;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository.mapper.DriverDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para conductores en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class DriverRepositoryAdapter implements DriverRepositoryPort {

    private final DriverMongoRepository repo;
    private final DriverDocumentMapper mapper;

    /**
     * Guarda un conductor
     *
     * @param d Conductor a guardar
     * @return Conductor guardado
     */
    @Override
    public Driver save(Driver d) {
        DriverDocument doc = mapper.toDocument(d);
        DriverDocument saved = repo.save(doc);
        return mapper.toDomain(saved);
    }

    /**
     * Busca un conductor por ID
     *
     * @param id ID del conductor
     * @return Conductor encontrado (opcional)
     */
    @Override
    public Optional<Driver> findById(Long id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    /**
     * Busca conductores por estado
     *
     * @param status Estado del conductor
     * @return Lista de conductores
     */
    @Override
    public List<Driver> findByStatus(String status) {
        return repo.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    /**
     * Busca conductores por nombre
     *
     * @param q Término de búsqueda
     * @return Lista de conductores
     */
    @Override
    public List<Driver> searchByName(String q) {
        return repo.findByNameContainingIgnoreCase(q).stream().map(mapper::toDomain).toList();
    }

    /**
     * Obtiene todos los conductores paginados
     *
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de conductores paginados
     */
    @Override
    public List<Driver> findAllPaged(int page, int size) {
        return repo.findAll().stream().map(mapper::toDomain)
                .skip((long) page * size).limit(size).toList();
    }

    /**
     * Obtiene todos los conductores
     *
     * @return Lista de todos los conductores
     */
    @Override
    public List<Driver> findAll() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }
}
