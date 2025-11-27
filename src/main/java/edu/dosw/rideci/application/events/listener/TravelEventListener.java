package edu.dosw.rideci.application.events.listener;

import edu.dosw.rideci.application.events.command.TravelCompletedCommand;
import edu.dosw.rideci.application.events.command.TravelCreatedCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import edu.dosw.rideci.infrastructure.configs.RabbitMQConfig;
import edu.dosw.rideci.application.service.TripEventService;

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
            System.out.println("✅ TravelCreatedEvent recibido en Admin");
            System.out.println("📦 " + event);
            tripEventService.processTripCreated(event);
            System.out.println("✅ procesado");
        } catch (Exception e) {
            System.err.println("❌ error procesando TravelCreatedEvent: " + e.getMessage());
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
            System.out.println("✅ TravelCompletedEvent recibido en Admin");
            System.out.println("📦 " + event);
            tripEventService.processTripFinished(event);
            System.out.println("✅ procesado");
        } catch (Exception e) {
            System.err.println("❌ error procesando TravelCompletedEvent: " + e.getMessage());
            throw e;
        }
    }
}
