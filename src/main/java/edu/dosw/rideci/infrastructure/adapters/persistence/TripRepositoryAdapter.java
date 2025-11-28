package edu.dosw.rideci.infrastructure.adapters.persistence;

import edu.dosw.rideci.application.port.out.TripRepositoryPort;
import edu.dosw.rideci.domain.model.TripMonitor;
import edu.dosw.rideci.domain.model.enums.TripStatus;
import edu.dosw.rideci.infrastructure.persistence.repository.TripMongoRepository;
import edu.dosw.rideci.infrastructure.persistence.repository.mapper.TripDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para viajes en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class TripRepositoryAdapter implements TripRepositoryPort {

    private final TripMongoRepository repo;
    private final TripDocumentMapper mapper;

    /**
     * Cuenta viajes por estado
     *
     * @param status Estado del viaje
     * @return Cantidad de viajes
     */
    @Override
    public long countByStatus(TripStatus status) {
        return repo.countByStatus(status);
    }

    /**
     * Cuenta viajes por rango de tiempo
     *
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Cantidad de viajes
     */
    @Override
    public long countByStartTimeBetween(LocalDateTime from, LocalDateTime to) {
        return repo.countByStartTimeBetween(from, to);
    }

    /**
     * Busca viajes por estado
     *
     * @param status Estado del viaje
     * @return Lista de viajes
     */
    @Override
    public List<TripMonitor> findByStatus(TripStatus status) {
        List<?> docs = repo.findByStatus(status);
        return mapper.toDomainList((List) docs);
    }

    /**
     * Obtiene todos los viajes paginados
     *
     * @param page Página a consultar
     * @param size Tamaño de la página
     * @return Lista de viajes paginados
     */
    @Override
    public List<TripMonitor> findAllPaged(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).getContent().stream()
                .map(mapper::toDomain).toList();
    }

    /**
     * Suma los ingresos en un rango de tiempo
     *
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Suma de ingresos
     */
    @Override
    public double sumIncomeBetween(LocalDateTime from, LocalDateTime to) {
        return repo.findByStartTimeBetween(from, to).stream()
                .mapToDouble(d -> d.getEstimatedCost()).sum();
    }

    /**
     * Suma el CO2 ahorrado en un rango de tiempo
     *
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Suma de CO2 ahorrado
     */
    @Override
    public double sumCo2Between(LocalDateTime from, LocalDateTime to) {
        return repo.findByStartTimeBetween(from, to).stream()
                .mapToDouble(d -> d.getCo2Saved()).sum();
    }

    /**
     * Obtiene un viaje por ID
     *
     * @param tripId ID del viaje
     * @return Viaje encontrado
     */
    @Override
    public TripMonitor getTripById(Long tripId) {
        Optional<?> maybe = repo.findById(tripId).map(mapper::toDomain);
        return (TripMonitor) maybe.orElse(null);
    }

    @Override
    public TripMonitor save(TripMonitor t) {
        var doc = mapper.toDocument(t);
        var savedDoc = repo.save(doc);
        return mapper.toDomain(savedDoc);
    }
}
