package edu.eci.ATENEA_Administration_BackEnd.application.service;

import edu.eci.ATENEA_Administration_BackEnd.domain.model.AdminAction;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Entity.AdminActionDocument;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository.AdminActionRepository;
import edu.eci.ATENEA_Administration_BackEnd.infrastructure.persistence.Repository.mapper.AdminActionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para registro de acciones de administrador en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AdminActionService {

    private final AdminActionRepository repo;
    private final AdminActionMapper mapper;


    /**
     * Registra una acción realizada por un administrador
     *
     * @param adminId ID del administrador
     * @param action Tipo de acción realizada
     * @param targetType Tipo de objetivo afectado
     * @param targetId ID del objetivo afectado
     * @param details Detalles adicionales de la acción
     * @return Documento de acción de administrador registrada
     */
    public AdminAction recordAction(Long adminId, String action, String targetType, String targetId, String details) {
        AdminAction domain = AdminAction.builder()
                .adminId(adminId)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .details(details)
                .at(LocalDateTime.now())
                .build();
        AdminActionDocument saved = repo.save(mapper.toDocument(domain));
        return mapper.toDomain(saved);
    }
}
