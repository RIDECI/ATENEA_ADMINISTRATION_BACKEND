package edu.dosw.rideci.application.service;

import edu.dosw.rideci.application.port.in.TripMonitoringUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import edu.dosw.rideci.application.port.out.TripRepositoryPort;
import edu.dosw.rideci.application.mapper.TripMapper;
import edu.dosw.rideci.domain.model.TripMonitor;
import edu.dosw.rideci.infrastructure.controller.dto.response.TripListItemDto;
import edu.dosw.rideci.infrastructure.controller.dto.response.TripDetailDto;
import edu.dosw.rideci.infrastructure.controller.dto.response.DashboardResponse;
import edu.dosw.rideci.domain.model.enums.TripStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de viajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class TripService implements TripMonitoringUseCase {

    private final TripRepositoryPort tripRepo;
    private final TripMapper mapper;


    /**
     * Lista viajes con filtros opcionales
     *
     * @param search Término de búsqueda (opcional)
     * @param status Estado del viaje (opcional)
     * @param type Tipo de viaje (opcional)
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de viajes
     */
    @Override
    public List<TripListItemDto> listTrips(String search, String status, String type, int page, int size) {
        if (status != null) {
            TripStatus ts;
            try {
                ts = TripStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
            return mapper.toListItems(tripRepo.findByStatus(ts));
        }
        return mapper.toListItems(tripRepo.findAllPaged(page, size));
    }

    /**
     * Obtiene viajes activos
     *
     * @return Lista de viajes en progreso
     */
    @Override
    public List<TripListItemDto> getActiveTrips() {
        List<TripMonitor> active = tripRepo.findByStatus(TripStatus.IN_PROGRESS);
        return mapper.toListItems(active);
    }

    /**
     * Obtiene detalle de un viaje
     *
     * @param tripId ID del viaje
     * @return Detalle del viaje
     * @throws java.util.NoSuchElementException Si el viaje no existe
     */
    @Override
    public TripDetailDto getTripDetail(Long tripId) {
        TripMonitor trip = tripRepo.getTripById(tripId);
        if (trip == null) throw new java.util.NoSuchElementException("Trip not found: " + tripId);
        return mapper.toDetail(trip);
    }

    /**
     * Obtiene métricas del dashboard
     *
     * @return Respuesta con métricas del dashboard
     */
    @Override
    public DashboardResponse getMetrics() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        int tripsToday = (int) tripRepo.countByStartTimeBetween(startOfDay, LocalDateTime.now());
        int tripsInProgress = (int) tripRepo.countByStatus(TripStatus.IN_PROGRESS);
        double income = tripRepo.sumIncomeBetween(startOfDay, LocalDateTime.now());
        double co2 = tripRepo.sumCo2Between(startOfDay, LocalDateTime.now());

        DashboardResponse resp = new DashboardResponse();
        resp.setTripsToday(tripsToday);
        resp.setTripsInProgress(tripsInProgress);
        resp.setIncome(income);
        resp.setCo2Reduced(co2);
        resp.setOpenSecurityReports(0);
        resp.setChangeSinceLastPeriod("+0%");
        resp.setLastUpdate(LocalDateTime.now());
        return resp;
    }
}
