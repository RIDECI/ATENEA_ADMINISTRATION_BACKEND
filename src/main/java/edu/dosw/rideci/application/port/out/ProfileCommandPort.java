package edu.dosw.rideci.application.port.out;

/**
 * Puerto de salida para comandos de perfil en RideECI
 *
 * @author RideECI
 * @version 1.0
 */
public interface ProfileCommandPort {
    /**
     * Publica un evento de perfil activado
     *
     * @param ev Evento a publicar
     * @param routingKey Clave de enrutamiento
     */
    void publishProfileActivated(Object ev, String routingKey);

    /**
     * Publica un evento de perfil suspendido
     *
     * @param ev Evento a publicar
     * @param routingKey Clave de enrutamiento
     */
    void publishProfileSuspended(Object ev, String routingKey);
}
