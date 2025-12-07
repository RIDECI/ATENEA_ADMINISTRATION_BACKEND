package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.Profile;
import java.util.List;

/**
 * Caso de uso para listado de perfiles en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface GetProfilesUseCase {
    /**
     * Lista perfiles con filtros opcionales
     *
     * @param search Término de búsqueda (opcional)
     * @param profileType Tipo de perfil (opcional)
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de perfiles
     */
    List<Profile> listProfiles(String search, String profileType, int page, int size);
}