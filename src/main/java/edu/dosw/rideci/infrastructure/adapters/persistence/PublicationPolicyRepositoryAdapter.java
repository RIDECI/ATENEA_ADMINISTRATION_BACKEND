package edu.dosw.rideci.infrastructure.adapters.persistence;

import edu.dosw.rideci.application.port.out.PublicationPolicyRepositoryPort;
import edu.dosw.rideci.infrastructure.persistence.entity.PublicationPolicyDocument;
import edu.dosw.rideci.infrastructure.persistence.repository.PublicationPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para políticas de publicación en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class PublicationPolicyRepositoryAdapter implements PublicationPolicyRepositoryPort {

    private final PublicationPolicyRepository repo;

    /**
     * Guarda una política de publicación
     *
     * @param doc Política de publicación a guardar
     * @return Política de publicación guardada
     */
    @Override
    public PublicationPolicyDocument save(PublicationPolicyDocument doc) {
        return repo.save(doc);
    }

    /**
     * Busca una política de publicación por ID
     *
     * @param id ID de la política
     * @return Política de publicación encontrada (opcional)
     */
    @Override
    public Optional<PublicationPolicyDocument> findById(String id) {
        return repo.findById(id);
    }

    /**
     * Obtiene todas las políticas de publicación
     *
     * @return Lista de todas las políticas de publicación
     */
    @Override
    public List<PublicationPolicyDocument> findAll() {
        return repo.findAll();
    }

    /**
     * Elimina una política de publicación por ID
     *
     * @param id ID de la política a eliminar
     */
    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }
}
