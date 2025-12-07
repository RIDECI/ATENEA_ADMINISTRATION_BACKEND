package edu.dosw.rideci.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.dosw.rideci.application.events.TravelCreatedEvent;
import edu.dosw.rideci.application.events.TravelCompletedEvent;
import edu.dosw.rideci.application.port.out.TripRepositoryPort;
import edu.dosw.rideci.domain.model.TripMonitor;
import edu.dosw.rideci.domain.model.enums.TripStatus;
import edu.dosw.rideci.domain.model.valueobjects.Location;
import edu.dosw.rideci.domain.model.enums.Status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio para procesamiento de eventos de viajes en RideECI
 * Maneja la creación, actualización y finalización de viajes en el sistema de monitoreo
 * Implementa lógica para mantener la consistencia del estado de los viajes
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class TripEventService {

    private final TripRepositoryPort tripRepo;
    private static final Logger LOG = LoggerFactory.getLogger(TripEventService.class);

    /**
     * Procesa un evento de viaje creado
     * Crea un nuevo TripMonitor o actualiza uno existente con los datos del evento
     * Incluye validación de datos y logging detallado
     *
     * @param e Evento de viaje creado con todos los datos del viaje
     */
    @Transactional
    public void processTripCreated(TravelCreatedEvent e) {
        if (e == null || e.getTravelId() == null) {
            LOG.warn("processTripCreated: evento nulo o travelId nulo");
            return;
        }
        LOG.debug("processTripCreated payload: {}", e);

        Long id = e.getTravelId();

        TripMonitor existing = tripRepo.getTripById(id);
        if (existing != null) {
            existing.setDriverId(e.getDriverId());
            existing.setOrigin(locationToString(e.getOrigin()));
            existing.setDestination(locationToString(e.getDestiny()));
            existing.setStartTime(e.getDepartureDateAndTime());
            existing.setEstimatedCost(Objects.requireNonNullElse(e.getEstimatedCost(), existing.getEstimatedCost()));
            existing.setPassengerIds(e.getPassengersId() == null ? Collections.emptyList() : e.getPassengersId());
            existing.setTravelType(e.getTravelType() != null ? e.getTravelType().name() : existing.getTravelType());
            existing.setStatus(mapStatus(e.getStatus()));
            tripRepo.save(existing);
            LOG.info("Updated TripMonitor travelId={} driverId={} status={}", id, e.getDriverId(), existing.getStatus());
        } else {
            TripMonitor t = new TripMonitor();
            t.setTripId(id);
            t.setDriverId(e.getDriverId());
            t.setStatus(mapStatus(e.getStatus()));
            t.setOrigin(locationToString(e.getOrigin()));
            t.setDestination(locationToString(e.getDestiny()));
            t.setStartTime(e.getDepartureDateAndTime() != null ? e.getDepartureDateAndTime() : LocalDateTime.now());
            t.setPassengerIds(e.getPassengersId() == null ? Collections.emptyList() : e.getPassengersId());
            t.setEstimatedCost(Objects.requireNonNullElse(e.getEstimatedCost(), 0.0));
            t.setTravelType(e.getTravelType() != null ? e.getTravelType().name() : null);
            tripRepo.save(t);
            LOG.info("Created TripMonitor travelId={} driverId={} status={}", id, t.getDriverId(), t.getStatus());
        }
    }

    /**
     * Procesa un evento de viaje finalizado
     * Actualiza el estado del viaje a COMPLETED y registra la hora de finalización
     * Maneja tanto viajes existentes como la creación de nuevos registros para viajes no encontrados
     *
     * @param e Evento de viaje completado con ID del viaje
     */
    @Transactional
    public void processTripFinished(TravelCompletedEvent e) {
        if (e == null || e.getTravelId() == null) {
            LOG.warn("processTripFinished: evento nulo o travelId nulo");
            return;
        }
        LOG.debug("processTripFinished payload: {}", e);

        Long id = e.getTravelId();
        var existing = tripRepo.getTripById(id);
        if (existing == null) {
            TripMonitor t = new TripMonitor();
            t.setTripId(id);
            t.setStatus(TripStatus.COMPLETED);
            t.setStartTime(LocalDateTime.now());
            t.setEndTime(LocalDateTime.now());
            tripRepo.save(t);
            LOG.info("Created TripMonitor (completed) travelId={}", id);
        } else {
            existing.setStatus(TripStatus.COMPLETED);
            existing.setEndTime(LocalDateTime.now());
            tripRepo.save(existing);
            LOG.info("Updated TripMonitor to COMPLETED travelId={}", id);
        }
    }

    /**
     * Mapea el estado del dominio de viajes al estado de monitoreo de viajes
     * Convierte los estados del sistema principal a los estados utilizados en el módulo de administración
     *
     * @param s Estado del dominio de viajes
     * @return Estado de monitoreo equivalente para TripMonitor
     */
    private TripStatus mapStatus(Status s) {
        if (s == null) return TripStatus.CREATED;
        switch (s) {
            case ACTIVE: return TripStatus.IN_PROGRESS;
            case IN_COURSE: return TripStatus.IN_PROGRESS;
            case COMPLETED: return TripStatus.COMPLETED;
            case CANCELLED: return TripStatus.CANCELLED;
            default: return TripStatus.CREATED;
        }
    }

    /**
     * Convierte una ubicación a string para almacenamiento
     * Formato: "latitud,longitud - dirección" (dirección opcional)
     *
     * @param loc Ubicación a convertir
     * @return String representando la ubicación en formato estandarizado
     */
    private String locationToString(Location loc) {
        if (loc == null) return null;
        return loc.getLatitude() + "," + loc.getLongitude()
                + (loc.getDirection() != null ? " - " + loc.getDirection() : "");
    }
}
