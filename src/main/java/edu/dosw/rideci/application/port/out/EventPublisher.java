package edu.dosw.rideci.application.port.out;

/**
 * Puerto de salida para publicación de eventos en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface EventPublisher {
    /**
     * Publica un evento en el sistema de mensajería
     *
     * @param event Evento a publicar
     * @param routingKey Clave de enrutamiento
     */
    void publish(Object event, String routingKey);

    /**
     * Publica un evento en el exchange y routingKey especificados.
     *
     * @param event Evento a publicar
     * @param exchange Exchange destino
     * @param routingKey Clave de enrutamiento
     */
    void publish(Object event, String exchange, String routingKey);
}
