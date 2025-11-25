package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.port.in.PublicationPolicyUseCase;
import edu.dosw.rideci.domain.model.PublicationPolicy;
import edu.dosw.rideci.domain.model.PolicyStrategyContext;
import edu.dosw.rideci.domain.model.PolicyStrategyFactory;
import edu.dosw.rideci.infrastructure.persistence.Entity.PublicationPolicyDocument;
import edu.dosw.rideci.infrastructure.persistence.Repository.PublicationPolicyRepository;
import edu.dosw.rideci.infrastructure.persistence.Repository.mapper.PublicationPolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de políticas de publicación en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class PublicationPolicyService implements PublicationPolicyUseCase {

    private final PublicationPolicyRepository repo;
    private final PublicationPolicyMapper mapper;

    /**
     * Crea una nueva política de publicación
     *
     * @param doc Política de publicación a crear
     * @return Política de publicación creada
     */
    @Override
    public PublicationPolicy createPolicy(PublicationPolicy doc)  {
        PublicationPolicyDocument saved = repo.save(mapper.toDocument(doc));
        return mapper.toDomain(saved);
    }

    /**
     * Actualiza una política de publicación existente
     *
     * @param id ID de la política a actualizar
     * @param update Política con datos actualizados
     * @return Política de publicación actualizada
     * @throws NoSuchElementException Si la política no existe
     */
    @Override
    public PublicationPolicy updatePolicy(String id, PublicationPolicy update) {
        PublicationPolicyDocument existing = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Policy not found: " + id));
        existing.setName(update.getName());
        existing.setStartTime(update.getStartTime());
        existing.setEndTime(update.getEndTime());
        existing.setEnabled(update.isEnabled());
        existing.setDescription(update.getDescription());
        existing.setAllowedDays(update.getAllowedDays() == null ? null :
                update.getAllowedDays().stream().map(java.time.DayOfWeek::name).toList());
        existing.setAllowedRoles(update.getAllowedRoles());
        PublicationPolicyDocument saved = repo.save(existing);
        return mapper.toDomain(saved);
    }

    /**
     * Obtiene una política de publicación por ID
     *
     * @param id ID de la política
     * @return Política de publicación encontrada
     * @throws NoSuchElementException Si la política no existe
     */
    @Override
    public PublicationPolicy getPolicy(String id) {
        return repo.findById(id).map(mapper::toDomain)
                .orElseThrow(() -> new NoSuchElementException("Policy not found: " + id));
    }

    /**
     * Lista todas las políticas de publicación
     *
     * @return Lista de políticas de publicación
     */
    @Override
    public List<PublicationPolicy> listPolicies() {
        return repo.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    /**
     * Elimina una política de publicación
     *
     * @param id ID de la política a eliminar
     */
    @Override
    public void deletePolicy(String id) {
        repo.deleteById(id);
    }




    /**
     * Encuentra una política que coincida con la fecha y hora especificadas
     *
     * @param at Fecha y hora a verificar (null para fecha/hora actual)
     * @return Política que coincide, si existe
     */
    @Override
    public Optional<PublicationPolicy> findMatchingPolicy(LocalDateTime at) {
        return findMatchingPolicy(at, null);
    }


    /**
     * Encuentra una política de publicación que coincida con la fecha/hora y contexto especificados
     * Aplica múltiples estrategias de validación para determinar la política aplicable
     *
     * @param at Fecha y hora a verificar
     * @param ctx Contexto de estrategia que incluye información del usuario
     * @return Política que coincide con todas las estrategias, si existe
     */
    @Override
    public Optional<PublicationPolicy> findMatchingPolicy(LocalDateTime at, PolicyStrategyContext ctx) {
        if (at == null) at = LocalDateTime.now();
        final LocalDateTime when = at;
        return repo.findAll().stream()
                .filter(PublicationPolicyDocument::isEnabled)
                .map(mapper::toDomain)
                .filter(p -> PolicyStrategyFactory.of(p).isSatisfied(p, when, ctx))
                .findFirst();
    }

    /**
     * Verifica si está permitido publicar en la fecha y hora especificadas
     *
     * @param at Fecha y hora a verificar
     * @return true si está permitido, false en caso contrario
     */
    @Override
    public boolean isAllowedAt(LocalDateTime at) {
        return findMatchingPolicy(at).isPresent();
    }
}
