package edu.eci.ATENEA_Administration_BackEnd.application.service;

import edu.eci.ATENEA_Administration_BackEnd.application.events.DriverApprovedEvent;
import edu.eci.ATENEA_Administration_BackEnd.application.events.DriverDocumentUploadedEvent;
import edu.eci.ATENEA_Administration_BackEnd.application.events.DriverRejectedEvent;
import edu.eci.ATENEA_Administration_BackEnd.application.port.in.ApproveDriverUseCase;
import edu.eci.ATENEA_Administration_BackEnd.application.port.in.GetDriversUseCase;
import edu.eci.ATENEA_Administration_BackEnd.application.port.out.DriverRepositoryPort;
import edu.eci.ATENEA_Administration_BackEnd.application.port.out.EventPublisher;
import edu.eci.ATENEA_Administration_BackEnd.domain.model.Driver;
import edu.eci.ATENEA_Administration_BackEnd.exceptions.DriverNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de conductores en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DriverService implements GetDriversUseCase, ApproveDriverUseCase {
    private final DriverRepositoryPort driverRepo;
    private final EventPublisher eventPublisher;
    private final AdminActionService adminActionService;


    /**
     * Lista conductores con filtros opcionales
     *
     * @param status Estado del conductor (opcional)
     * @param search Término de búsqueda (opcional)
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de conductores
     */
    @Override
    public List<Driver> listDrivers(String status, String search, int page, int size) {
        if (search != null && !search.isBlank()) return driverRepo.searchByName(search);
        if (status != null) return driverRepo.findByStatus(status);
        return driverRepo.findByStatus("VERIFIED");
    }


    /**
     * Obtiene un conductor por ID
     *
     * @param id ID del conductor
     * @return Conductor encontrado
     * @throws DriverNotFoundException Si el conductor no existe
     */
    @Override
    public Driver getDriver(Long id) {
        return driverRepo.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found: " + id));
    }

    /**
     * Aprueba un conductor en el sistema
     *
     * @param driverId ID del conductor a aprobar
     * @param adminId ID del administrador que realiza la acción
     */
    @Override
    public void approveDriver(Long driverId, Long adminId) {
        Driver d = getDriver(driverId);
        d.setStatus("VERIFIED");
        d.setVerifiedAt(LocalDateTime.now());
        driverRepo.save(d);

        adminActionService.recordAction(adminId, "APPROVE_DRIVER", "DRIVER", String.valueOf(driverId), "approved driver");
        DriverApprovedEvent event = DriverApprovedEvent.builder()
                .driverId(driverId)
                .adminId(adminId)
                .verifiedAt(d.getVerifiedAt())
                .build();

        eventPublisher.publish(event, "admin.driver.approved");
    }

    /**
     * Rechaza un conductor en el sistema
     *
     * @param driverId ID del conductor a rechazar
     * @param adminId ID del administrador que realiza la acción
     * @param reason Motivo del rechazo
     */
    @Override
    public void rejectDriver(Long driverId, Long adminId, String reason) {
        Driver d = getDriver(driverId);
        d.setStatus("REJECTED");
        d.setRejectionReason(reason);
        driverRepo.save(d);

        adminActionService.recordAction(adminId, "REJECT_DRIVER", "DRIVER", String.valueOf(driverId), "reason=" + reason);
        DriverRejectedEvent event = DriverRejectedEvent.builder()
                .driverId(driverId)
                .adminId(adminId)
                .reason(reason)
                .rejectedAt(LocalDateTime.now())
                .build();

        eventPublisher.publish(event, "admin.driver.rejected");
    }

    /**
     * Agrega referencia de documento a conductor
     *
     * @param driverId ID del conductor
     * @param fileRef Referencia del archivo
     * @param type Tipo de documento
     * @param uploadedBy ID del usuario que subió el documento
     */
    public void addDocumentRef(Long driverId, String fileRef, String type, Long uploadedBy) {
        Driver d = getDriver(driverId);
        if (d.getDocumentRefs() == null) {
            d.setDocumentRefs(new java.util.ArrayList<>());
        }
        d.getDocumentRefs().add(fileRef);
        driverRepo.save(d);

        DriverDocumentUploadedEvent event = DriverDocumentUploadedEvent.builder()
                .driverId(driverId)
                .fileId(fileRef)
                .type(type)
                .uploadedBy(uploadedBy)
                .uploadedAt(LocalDateTime.now())
                .build();

        eventPublisher.publish(event, "admin.driver.document.uploaded");
    }
}
