package edu.eci.ATENEA_Administration_BackEnd.application.service;

import edu.eci.ATENEA_Administration_BackEnd.domain.model.PublicationPolicy;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.PublicationPolicyDocument;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository.PublicationPolicyRepository;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository.mapper.PublicationPolicyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class PublicationPolicyService {

    private final PublicationPolicyRepository repo;
    private final PublicationPolicyMapper mapper;

    /**
     * Crea una nueva política de publicación
     *
     * @param doc Política de publicación a crear
     * @return Política de publicación creada
     */
    public PublicationPolicy createPolicy(PublicationPolicy doc) {
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
    public PublicationPolicy updatePolicy(String id, PublicationPolicy update) {
        PublicationPolicyDocument existing = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Policy not found: " + id));
        existing.setName(update.getName());
        existing.setStartTime(update.getStartTime());
        existing.setEndTime(update.getEndTime());
        existing.setEnabled(update.isEnabled());
        existing.setDescription(update.getDescription());
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
    public PublicationPolicy getPolicy(String id) {
        return repo.findById(id).map(mapper::toDomain)
                .orElseThrow(() -> new NoSuchElementException("Policy not found: " + id));
    }

    /**
     * Lista todas las políticas de publicación
     *
     * @return Lista de políticas de publicación
     */
    public List<PublicationPolicy> listPolicies() {
        return repo.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    /**
     * Elimina una política de publicación
     *
     * @param id ID de la política a eliminar
     */
    public void deletePolicy(String id) {
        repo.deleteById(id);
    }


    /**
     * Encuentra una política que coincida con la fecha y hora especificadas
     *
     * @param at Fecha y hora a verificar (null para fecha/hora actual)
     * @return Política que coincide, si existe
     */
    public Optional<PublicationPolicy> findMatchingPolicy(LocalDateTime at) {
        if (at == null) at = LocalDateTime.now();
        LocalTime t = at.toLocalTime();
        return repo.findAll().stream()
                .filter(PublicationPolicyDocument::isEnabled)
                .filter(p -> matchesTime(mapper.toDomain(p), t))
                .findFirst()
                .map(mapper::toDomain);
    }

    /**
     * Verifica si está permitido publicar en la fecha y hora especificadas
     *
     * @param at Fecha y hora a verificar
     * @return true si está permitido, false en caso contrario
     */
    public boolean isAllowedAt(LocalDateTime at) {
        return findMatchingPolicy(at).isPresent();
    }

    /**
     * Verifica si una política coincide con la hora especificada
     *
     * @param p Política de publicación
     * @param t Hora a verificar
     * @return true si la hora está dentro del rango de la política
     */
    private boolean matchesTime(PublicationPolicy p, LocalTime t) {
        LocalTime start = p.getStartTime();
        LocalTime end = p.getEndTime();

        if (start == null || end == null) {
            return true;
        }
        if (start.equals(end)) {
            return true;
        }
        if (!start.isAfter(end)) {
            return (!t.isBefore(start)) && (!t.isAfter(end));
        } else {
            return (!t.isBefore(start)) || (!t.isAfter(end));
        }
    }
}
