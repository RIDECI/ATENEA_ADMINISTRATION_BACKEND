package edu.dosw.rideci.application.events.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.dosw.rideci.application.events.UserEvent;
import edu.dosw.rideci.application.mapper.UserEventMapper;
import edu.dosw.rideci.application.port.in.CreateUserUseCase;
import edu.dosw.rideci.domain.model.User;
import edu.dosw.rideci.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Listener único y robusto para eventos de usuario.
 * - Recibe payload crudo como Map (permite aceptar campos "id" o "userId" en varias formas)
 * - Intenta normalizar userId y luego delega al mapper + CreateUserUseCase (upsert)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final CreateUserUseCase userSync;
    private final UserEventMapper mapper;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Recibe al usuario
     * @param payload
     */
    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void receiveRaw(Map<String,Object> payload) {
        log.info("[UserEventListener] RAW payload -> {}", payload);

        try {
            UserEvent ev = objectMapper.convertValue(payload, UserEvent.class);
            if (ev.getUserId() == null) {
                Object maybeUserId = payload.get("userId");
                if (maybeUserId == null) {
                    maybeUserId = payload.get("id");
                }
                if (maybeUserId != null) {
                    Long parsed = tryParseLong(maybeUserId);
                    if (parsed != null) {
                        ev.setUserId(parsed);
                    } else {
                        log.warn("[UserEventListener] couldn't parse user id from payload field (value={}) — ignoring userId", maybeUserId);
                    }
                }
            }

            if (ev == null || ev.getUserId() == null) {
                log.warn("[UserEventListener] missing userId after parsing, ignoring payload -> {}", payload);
                return;
            }

            User domain = mapper.toDomain(ev);
            User saved = userSync.createUser(domain);
            log.info("[UserEventListener] User synced: userId={}, savedId={}", ev.getUserId(), saved != null ? saved.getId() : "null");

        } catch (Exception ex) {
            log.error("[UserEventListener] error processing user event: {}", ex.getMessage(), ex);
        }
    }

    private Long tryParseLong(Object val) {
        if (val == null) return null;
        try {
            if (val instanceof Number number) {
                return number.longValue();
            } else {
                String s = String.valueOf(val).trim();
                if (s.isEmpty()) return null;
                return Long.valueOf(s);
            }
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

