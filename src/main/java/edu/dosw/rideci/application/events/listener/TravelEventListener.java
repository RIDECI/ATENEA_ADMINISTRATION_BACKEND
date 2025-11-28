package edu.dosw.rideci.application.events.listener;

import edu.dosw.rideci.application.events.command.TravelCompletedCommand;
import edu.dosw.rideci.application.events.command.TravelCreatedCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import edu.dosw.rideci.infrastructure.configs.RabbitMQConfig;
import edu.dosw.rideci.application.service.TripEventService;

import static org.apache.xmlbeans.impl.common.XBeanDebug.LOG;

/**
 * Listener para eventos de viajes en RideECI
 * Procesa creación y finalización de viajes
 *
 * @author RideECI
 * @version 1.0
 */
@Component
public class TravelEventListener {

    private final TripEventService tripEventService;

    public TravelEventListener(TripEventService tripEventService) {
        this.tripEventService = tripEventService;
    }

    /**
     * Procesa eventos de viajes creados
     *
     * @param event Comando de viaje creado
     */
    @RabbitListener(queues = RabbitMQConfig.TRIP_CREATED_QUEUE)
    public void handleTripCreated(TravelCreatedCommand event) {
        try {
            LOG.info("✅ TravelCreatedEvent recibido en Admin, id={}", event.getTravelId());
            LOG.debug("📦 event payload: {}", event);
            tripEventService.processTripCreated(event);
            LOG.info("✅ procesado travelId={}", event.getTravelId());
        } catch (Exception e) {
            LOG.error("❌ error procesando TravelCreatedEvent: {}", e.getMessage(), e);
            throw e;
        }
    }
    /**
     * Procesa eventos de viajes finalizados
     *
     * @param event Comando de viaje completado
     */
    @RabbitListener(queues = RabbitMQConfig.TRIP_FINISHED_QUEUE)
    public void handleTripFinished(TravelCompletedCommand event) {
        try {
            if (event == null) {
                LOG.warn("handleTripFinished: recibido evento nulo, ignorando");
                return;
            }
            LOG.info("✅ TravelCompletedEvent recibido en Admin: travelId={}", event.getTravelId());
            LOG.debug("📦 TravelCompletedCommand payload: {}", event);
            tripEventService.processTripFinished(event);
            LOG.info("✅ TravelCompletedEvent procesado correctamente: travelId={}", event.getTravelId());
        } catch (Exception e) {
            LOG.error("❌ error procesando TravelCompletedEvent: travelId={}, message={}",
                    event != null ? event.getTravelId() : "null", e.getMessage(), e);
            throw e;
        }
    }
}
