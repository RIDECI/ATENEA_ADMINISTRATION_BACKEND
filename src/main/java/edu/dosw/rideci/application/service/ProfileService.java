package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.events.ProfileEvent;
import edu.dosw.rideci.application.port.in.GetProfileUseCase;
import edu.dosw.rideci.application.port.in.GetProfilesUseCase;
import edu.dosw.rideci.application.port.in.ManageProfileUseCase;
import edu.dosw.rideci.application.port.out.ProfileRepositoryPort;
import edu.dosw.rideci.application.port.out.ProfileClientPort;
import edu.dosw.rideci.domain.model.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de perfiles en RideECI
 * Implementa casos de uso para consulta y gestión de perfiles de usuarios
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ProfileService implements GetProfilesUseCase, GetProfileUseCase, ManageProfileUseCase {

    private final ProfileRepositoryPort profileRepo;
    private final ProfileClientPort profileClient; // para enviar comandos al servicio de perfiles

    /**
     * Lista perfiles con filtros opcionales
     *
     * @param search Término de búsqueda
     * @param profileType Tipo de perfil
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de perfiles
     */
    @Override
    public List<Profile> listProfiles(String search, String profileType, int page, int size) {
        if (search != null && !search.isBlank()) {
            return profileRepo.searchByName(search);
        }
        if (profileType != null && !profileType.isBlank()) {
            return profileRepo.findByProfileType(profileType);
        }
        return profileRepo.findAllPaged(page, size);
    }

    /**
     * Obtiene un perfil por ID de usuario
     *
     * @param userId ID del usuario
     * @return Perfil encontrado
     */
    @Override
    public Optional<Profile> getByUserId(Long userId) {
        return profileRepo.findByUserId(userId);
    }

    /**
     * Crea o actualiza un perfil desde un evento
     *
     * @param ev Evento de perfil con datos a upsert
     * @return Perfil creado o actualizado
     */
    @Override
    public Profile upsertFromEvent(ProfileEvent ev) {
        String effectiveType = ev.getProfileType() != null ? ev.getProfileType().name() : ev.getRole();
        Profile p = Profile.builder()
                .userId(ev.getUserId())
                .name(ev.getName())
                .email(ev.getEmail())
                .phoneNumber(ev.getPhoneNumber())
                .profileType(effectiveType)
                .state(ev.getState() == null ? "ACTIVE" : ev.getState())
                .build();

        return profileRepo.save(p);
    }


    /**
     * Activa un perfil de usuario
     * Actualiza el estado local y envía comando al servicio de perfiles
     *
     * @param userId ID del usuario
     * @param adminId ID del administrador que realiza la acción
     * @param profileType Tipo de perfil a activar
     */
    @Override
    public void activateProfile(Long userId, Long adminId, String profileType) {
        profileRepo.findByUserId(userId).ifPresent(p -> {
            p.setState("ACTIVE");
            profileRepo.save(p);
        });

        if (profileType != null && !profileType.isBlank()) {
            profileClient.activateProfilesForUserByType(userId, profileType);
        } else {
            profileClient.activateProfilesForUser(userId);
        }
    }

    /**
     * Desactiva un perfil de usuario
     * Actualiza el estado local y envía comando al servicio de perfiles
     *
     * @param userId ID del usuario
     * @param adminId ID del administrador que realiza la acción
     * @param profileType Tipo de perfil a desactivar
     */
    @Override
    public void deactivateProfile(Long userId, Long adminId, String profileType) {
        profileRepo.findByUserId(userId).ifPresent(p -> {
            p.setState("INACTIVE");
            profileRepo.save(p);
        });

        if (profileType != null && !profileType.isBlank()) {
            profileClient.deactivateProfilesForUserByType(userId, profileType);
        } else {
            profileClient.deactivateProfilesForUser(userId);
        }
    }
}
