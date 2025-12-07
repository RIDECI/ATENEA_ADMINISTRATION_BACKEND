package edu.dosw.rideci.infrastructure.adapters.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import edu.dosw.rideci.application.port.out.ProfileRepositoryPort;
import edu.dosw.rideci.domain.model.Profile;
import edu.dosw.rideci.infrastructure.persistence.entity.ProfileDocument;
import edu.dosw.rideci.infrastructure.persistence.repository.ProfileMongoRepository;

/**
 * Adaptador de persistencia para perfiles en RideECI
 * Implementa el puerto de repositorio utilizando MongoDB
 *
 * @author RideECI
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ProfileRepositoryAdapter implements ProfileRepositoryPort {

    private final ProfileMongoRepository repo;

    /**
     * Guarda un perfil (upsert basado en userId)
     *
     * @param profile Perfil a guardar
     * @return Perfil guardado
     */
    @Override
    public Profile save(Profile profile) {
        ProfileDocument doc = repo.findByUserId(profile.getUserId())
                .map(existing -> updateExistingDocument(existing, profile))
                .orElseGet(() -> createNewDocument(profile));

        ProfileDocument saved = repo.save(doc);
        return toDomain(saved);
    }

    /**
     * Actualiza un documento existente con nuevos datos del perfil
     *
     * @param existing Documento existente
     * @param profile Nuevos datos del perfil
     * @return Documento actualizado
     */
    private ProfileDocument updateExistingDocument(ProfileDocument existing, Profile profile) {
        existing.setName(profile.getName());
        existing.setEmail(profile.getEmail());
        existing.setPhoneNumber(profile.getPhoneNumber());
        existing.setProfileType(profile.getProfileType());
        existing.setState(profile.getState());
        existing.setUpdatedAt(LocalDateTime.now());
        return existing;
    }

    /**
     * Crea un nuevo documento desde un perfil
     *
     * @param profile Perfil a convertir
     * @return Nuevo documento
     */
    private ProfileDocument createNewDocument(Profile profile) {
        return ProfileDocument.builder()
                .userId(profile.getUserId())
                .name(profile.getName())
                .email(profile.getEmail())
                .phoneNumber(profile.getPhoneNumber())
                .profileType(profile.getProfileType())
                .state(profile.getState())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Convierte ProfileDocument a Profile (modelo de dominio)
     *
     * @param d Documento de perfil
     * @return Modelo de dominio Profile
     */
    private Profile toDomain(ProfileDocument d) {
        return Profile.builder()
                .userId(d.getUserId())
                .name(d.getName())
                .email(d.getEmail())
                .phoneNumber(d.getPhoneNumber())
                .profileType(d.getProfileType())
                .state(d.getState())
                .build();
    }

    /**
     * Busca un perfil por ID de usuario
     *
     * @param userId ID del usuario
     * @return Perfil encontrado (opcional)
     */
    @Override
    public Optional<Profile> findByUserId(Long userId) {
        return repo.findByUserId(userId).map(this::toDomain);
    }

    /**
     * Obtiene todos los perfiles paginados
     *
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de perfiles paginados
     */
    @Override
    public List<Profile> findAllPaged(int page, int size) {
        var docs = repo.findAll(org.springframework.data.domain.PageRequest.of(page, size)).getContent();
        return docs.stream().map(this::toDomain).toList();
    }

    /**
     * Busca perfiles por nombre (búsqueda case-insensitive)
     *
     * @param q Término de búsqueda
     * @return Lista de perfiles que coinciden
     */
    @Override
    public List<Profile> searchByName(String q) {
        return repo.findByNameContainingIgnoreCase(q).stream().map(this::toDomain).toList();
    }

    /**
     * Busca perfiles por tipo
     *
     * @param profileType Tipo de perfil
     * @return Lista de perfiles del tipo especificado
     */
    @Override
    public List<Profile> findByProfileType(String profileType) {
        return repo.findByProfileType(profileType).stream().map(this::toDomain).toList();
    }
}