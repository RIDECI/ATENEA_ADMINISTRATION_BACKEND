package edu.dosw.rideci.application.port.in;

import edu.dosw.rideci.domain.model.PublicationPolicy;
import edu.dosw.rideci.domain.model.PolicyStrategyContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para gestión de políticas de publicación en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface PublicationPolicyUseCase {
    /**
     * Crea una nueva política de publicación
     *
     * @param policy Política de publicación a crear
     * @return Política de publicación creada
     */
    PublicationPolicy createPolicy(PublicationPolicy policy);

    /**
     * Actualiza una política de publicación existente
     *
     * @param id ID de la política a actualizar
     * @param policy Política con datos actualizados
     * @return Política de publicación actualizada
     */
    PublicationPolicy updatePolicy(String id, PublicationPolicy policy);

    /**
     * Obtiene una política de publicación por ID
     *
     * @param id ID de la política
     * @return Política de publicación encontrada
     */
    PublicationPolicy getPolicy(String id);

    /**
     * Lista todas las políticas de publicación
     *
     * @return Lista de políticas de publicación
     */
    List<PublicationPolicy> listPolicies();

    /**
     * Elimina una política de publicación
     *
     * @param id ID de la política a eliminar
     */
    void deletePolicy(String id);

    /**
     * Encuentra una política que coincida con la fecha y hora especificadas
     *
     * @param at Fecha y hora a verificar
     * @return Política que coincide, si existe
     */
    Optional<PublicationPolicy> findMatchingPolicy(LocalDateTime at);

    /**
     * Encuentra una política que coincida con la fecha/hora y contexto especificados
     *
     * @param at Fecha y hora a verificar
     * @param ctx Contexto de estrategia con información del usuario
     * @return Política que coincide, si existe
     */
    Optional<PublicationPolicy> findMatchingPolicy(LocalDateTime at, PolicyStrategyContext ctx);

    /**
     * Verifica si está permitido publicar en la fecha y hora especificadas
     *
     * @param at Fecha y hora a verificar
     * @return true si está permitido, false en caso contrario
     */
    boolean isAllowedAt(LocalDateTime at);
}