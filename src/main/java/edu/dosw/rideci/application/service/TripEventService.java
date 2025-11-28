package edu.dosw.rideci.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.dosw.rideci.application.port.out.TripRepositoryPort;
import edu.dosw.rideci.application.events.command.TravelCreatedCommand;
import edu.dosw.rideci.application.events.command.TravelCompletedCommand;
import edu.dosw.rideci.domain.model.TripMonitor;
import edu.dosw.rideci.domain.model.enums.TripStatus;
import edu.dosw.rideci.domain.model.valueobjects.Location;
import edu.dosw.rideci.domain.model.enums.Status;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.apache.xmlbeans.impl.common.XBeanDebug.LOG;

/**
 * Servicio para procesamiento de eventos de viajes en RideECI
 * Maneja la creación, actualización y finalización de viajes en el sistema de monitoreo
 *
 * @author RideECI
 * @version 1.0
 */
@Service
public class TripEventService {

    private final TripRepositoryPort tripRepo;

    public TripEventService(TripRepositoryPort tripRepo) {
        this.tripRepo = tripRepo;
    }

    /**
     * Procesa un evento de viaje creado
     * Valida el evento y crea o actualiza el monitoreo del viaje
     * Rechaza viajes programados para domingo
     *
     * @param e Comando de viaje creado
     */
    @Transactional
    public void processTripCreated(TravelCreatedCommand e) {
        if (e == null || e.getTravelId() == null) {
            LOG.warn("TripEventService.processTripCreated: evento nulo o travelId nulo");
            return;
        }

        LOG.debug("processTripCreated payload: {}", e);

        if (e.getDepartureDateAndTime() != null &&
                e.getDepartureDateAndTime().getDayOfWeek() == DayOfWeek.SUNDAY) {
            LOG.info("⛔ Rechazado TravelCreated con departureDate en DOMINGO. travelId={}", e.getTravelId());
            return;
        }

        Long id = e.getTravelId();

        TripMonitor existing = tripRepo.getTripById(id);
        if (existing != null) {
            existing.setDriverId(e.getDriverId());
            existing.setStatus(mapStatus(e.getState()));
            existing.setOrigin(locationToString(e.getOrigin()));
            existing.setDestination(locationToString(e.getDestiny()));
            existing.setStartTime(e.getDepartureDateAndTime());
            existing.setEstimatedCost(Objects.requireNonNullElse(e.getEstimatedCost(), existing.getEstimatedCost()));
            existing.setCo2Saved(Objects.requireNonNullElse(e.getCo2Saved(), existing.getCo2Saved()));
            existing.setPassengerIds(e.getPassengersId());
            existing.setTravelType(e.getTravelType() != null ? e.getTravelType().name() : existing.getTravelType());
            tripRepo.save(existing);
            LOG.info("Updated TripMonitor travelId={} driverId={} status={}", id, e.getDriverId(), existing.getStatus());
            LOG.debug("Updated TripMonitor content: {}", existing);
            return;
        }

        TripMonitor t = new TripMonitor();
        t.setTripId(id);
        t.setDriverId(e.getDriverId());
        t.setStatus(mapStatus(e.getState()));
        t.setOrigin(locationToString(e.getOrigin()));
        t.setDestination(locationToString(e.getDestiny()));
        t.setStartTime(e.getDepartureDateAndTime() != null ? e.getDepartureDateAndTime() : LocalDateTime.now());
        t.setPassengerIds(e.getPassengersId());
        t.setEstimatedCost(Objects.requireNonNullElse(e.getEstimatedCost(), 0.0));
        t.setCo2Saved(Objects.requireNonNullElse(e.getCo2Saved(), 0.0));
        t.setTravelType(e.getTravelType() != null ? e.getTravelType().name() : null);
        tripRepo.save(t);
        LOG.info("Created TripMonitor travelId={} driverId={} status={}", id, t.getDriverId(), t.getStatus());
        LOG.debug("New TripMonitor content: {}", t);
    }

    /**
     * Procesa un evento de viaje finalizado
     * Actualiza el estado del viaje a COMPLETED y registra la hora de finalización
     *
     * @param e Comando de viaje completado
     */
    @Transactional
    public void processTripFinished(TravelCompletedCommand e) {
        if (e == null || e.getTravelId() == null) {
            LOG.warn("TripEventService.processTripFinished: evento nulo o travelId nulo");
            return;
        }

        LOG.debug("processTripFinished payload: {}", e);

        Long id = e.getTravelId();
        TripMonitor existing = tripRepo.getTripById(id);
        if (existing == null) {
            TripMonitor t = new TripMonitor();
            t.setTripId(id);
            t.setStatus(TripStatus.COMPLETED);
            t.setStartTime(LocalDateTime.now());
            t.setEndTime(LocalDateTime.now());
            tripRepo.save(t);
            return;
        }
        existing.setStatus(TripStatus.COMPLETED);
        existing.setEndTime(LocalDateTime.now());
        tripRepo.save(existing);
    }

    /**
     * Mapea el estado del dominio de viajes al estado de monitoreo
     *
     * @param s Estado del dominio de viajes
     * @return Estado de monitoreo equivalente
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
     *
     * @param loc Ubicación a convertir
     * @return String representando la ubicación
     */
    private String locationToString(Location loc) {
        if (loc == null) return null;
        return loc.getLatitude() + "," + loc.getLongitude()
                + (loc.getDirection() != null ? " - " + loc.getDirection() : "");
    }
}
