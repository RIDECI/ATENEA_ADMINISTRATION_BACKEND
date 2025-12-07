package edu.dosw.rideci.application.events.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.dosw.rideci.application.events.ProfileEvent;
import edu.dosw.rideci.application.service.ProfileService;
import edu.dosw.rideci.application.port.out.DriverRepositoryPort;
import edu.dosw.rideci.domain.model.Driver;
import edu.dosw.rideci.infrastructure.configs.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Listener para eventos de perfiles en RideECI
 * Procesa la creación de perfiles y sincroniza con la colección de conductores
 *
 * @author RideECI
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileEventListener {

    private final DriverRepositoryPort driverRepo;
    private final ProfileService profileService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Procesa eventos de creación de perfiles
     * Realiza upsert del perfil y sincroniza la colección de conductores si corresponde
     *
     * @param payload Payload del evento (puede ser ProfileEvent, Map u otro objeto)
     */
    @RabbitListener(queues = RabbitMQConfig.PROFILE_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onProfileCreated(Object payload) {
        ProfileEvent ev = parsePayload(payload);
        if (ev == null || ev.getUserId() == null) {
            log.warn("profile.created -> payload inválido o sin userId: payloadClass={}, payload={}",
                    payload == null ? "null" : payload.getClass(), payload);
            return;
        }

        try {
            var saved = profileService.upsertFromEvent(ev);
            log.info("Profile upserted in admin.profiles userId={}, type={}", saved.getUserId(), saved.getProfileType());

            String effectiveType = saved.getProfileType();
            if (effectiveType != null && "DRIVER".equalsIgnoreCase(effectiveType)) {
                upsertDriver(ev);
            } else {
                log.debug("profile.created not driver -> skipping driver creation userId={}", ev.getUserId());
            }

        } catch (Exception ex) {
            log.error("Error procesando profile.created userId={}: {}", ev.getUserId(), ex.getMessage(), ex);
        }
    }


    /**
     * Convierte el payload recibido a ProfileEvent
     * Soporta múltiples formatos: ProfileEvent directo, Map y conversión genérica
     *
     * @param payload Payload a parsear
     * @return ProfileEvent parseado o null si no se puede convertir
     */
    private ProfileEvent parsePayload(Object payload) {
        try {
            if (payload instanceof ProfileEvent pEvent) return pEvent;

            if (payload instanceof Map<?,?> mapPayload) {
                @SuppressWarnings("unchecked")
                Map<String,Object> map = (Map<String,Object>) mapPayload;
                return profileEventFromMap(map);
            }

            return objectMapper.convertValue(payload, ProfileEvent.class);
        } catch (Exception ex) {
            log.warn("No pude convertir payload a ProfileEvent: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * Crea ProfileEvent desde un Map, manejando posibles variaciones en nombres de campos
     *
     * @param map Mapa con datos del evento
     * @return ProfileEvent creado
     */
    private ProfileEvent profileEventFromMap(Map<String,Object> map) {
        ProfileEvent ev = objectMapper.convertValue(map, ProfileEvent.class);

        if (ev.getUserId() == null) {
            Object maybe = map.get("userId");
            if (maybe == null) maybe = map.get("id");
            Long parsed = tryParseLong(maybe);
            ev.setUserId(parsed);
        }

        return ev;
    }

    /**
     * Crea o actualiza un conductor basado en el evento de perfil
     * Mantiene sincronizada la colección de conductores
     *
     * @param ev Evento de perfil con datos del usuario
     */
    private void upsertDriver(ProfileEvent ev) {
        Driver d = Driver.builder()
                .driverId(null)
                .userId(ev.getUserId())
                .name(ev.getName())
                .email(ev.getEmail())
                .phone(ev.getPhoneNumber())
                .status("PENDING")
                .build();

        Optional<Driver> maybe = driverRepo.findByUserId(ev.getUserId());
        if (maybe.isPresent()) {
            updateExistingDriver(maybe.get(), d);
        } else {
            driverRepo.save(d);
            log.info("Driver creado desde profile.created userId={}", ev.getUserId());
        }
    }

    /**
     * Actualiza un conductor existente con nuevos datos del evento
     * Preserva el estado actual a menos que sea PENDING
     *
     * @param existing Conductor existente
     * @param incoming Nuevos datos del conductor
     */
    private void updateExistingDriver(Driver existing, Driver incoming) {
        existing.setName(incoming.getName());
        existing.setEmail(incoming.getEmail());
        existing.setPhone(incoming.getPhone());
        if (existing.getStatus() == null || existing.getStatus().isBlank() || "PENDING".equals(existing.getStatus())) {
            existing.setStatus(incoming.getStatus());
        }
        driverRepo.save(existing);
        log.info("Driver actualizado desde profile.created userId={}", existing.getUserId());
    }

    /**
     * Intenta convertir un objeto a Long
     * Soporta Number y String
     *
     * @param o Objeto a convertir
     * @return Long convertido o null si no es posible
     */
    private Long tryParseLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        if (o instanceof String s) {
            try { return Long.parseLong(s.trim()); } catch (NumberFormatException ex) { return null; }
        }
        return null;
    }
}
