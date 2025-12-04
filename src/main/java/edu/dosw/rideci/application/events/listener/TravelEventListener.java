package edu.dosw.rideci.application.events.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.dosw.rideci.application.events.TravelCreatedEvent;
import edu.dosw.rideci.application.events.TravelCompletedEvent;
import edu.dosw.rideci.application.service.TripEventService;
import edu.dosw.rideci.infrastructure.configs.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;



/**
 * Listener para eventos de viajes en RideECI
 * Procesa creación y finalización de viajes para mantener el monitoreo actualizado
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TravelEventListener {

    private final TripEventService tripEventService;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Procesa eventos de viajes creados
     *
     * @param event Evento de viaje creado
     */
    @RabbitListener(queues = RabbitMQConfig.TRIP_CREATED_QUEUE)
    public void handleTripCreated(TravelCreatedEvent event) {
        try {
            if (event == null || event.getTravelId() == null) {
                log.warn("handleTripCreated: evento nulo o travelId nulo");
                return;
            }
            log.info("✅ TravelCreatedEvent recibido: travelId={}", event.getTravelId());
            tripEventService.processTripCreated(event);
            log.info("✅ TravelCreatedEvent procesado: travelId={}", event.getTravelId());
        } catch (Exception ex) {
            log.error("❌ Error procesando TravelCreatedEvent travelId={} : {}",
                    event != null ? event.getTravelId() : "null", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Procesa eventos de viajes finalizados
     *
     * @param event Evento de viaje completado
     */
    @RabbitListener(queues = RabbitMQConfig.TRIP_FINISHED_QUEUE)
    public void handleTripFinished(TravelCompletedEvent event) {
        try {
            if (event == null || event.getTravelId() == null) {
                log.warn("handleTripFinished: evento nulo o travelId nulo");
                return;
            }
            log.info("✅ TravelCompletedEvent recibido: travelId={}", event.getTravelId());
            tripEventService.processTripFinished(event);
            log.info("✅ TravelCompletedEvent procesado: travelId={}", event.getTravelId());
        } catch (Exception ex) {
            log.error("❌ Error procesando TravelCompletedEvent travelId={} : {}",
                    event != null ? event.getTravelId() : "null", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
    @RabbitListener(queues = RabbitMQConfig.TRIP_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleRawTripCreated(Map<String, Object> payload) {
        log.info("RAW travel.created payload -> {}", payload);
        try {
            TravelCreatedEvent e = mapper.convertValue(payload, TravelCreatedEvent.class);
            handleTripCreated(e);
        } catch (Exception ex) {
            log.error("No se pudo mapear raw payload a TravelCreatedEvent: {}", ex.getMessage(), ex);
        }
    }
    **/

}
