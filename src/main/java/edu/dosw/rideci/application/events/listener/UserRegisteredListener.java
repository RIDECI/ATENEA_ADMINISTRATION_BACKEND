package edu.dosw.rideci.application.events.listener;

import edu.dosw.rideci.application.events.UserEvent;
import edu.dosw.rideci.application.mapper.UserEventMapper;
import edu.dosw.rideci.application.port.in.CreateUserUseCase;
import edu.dosw.rideci.infrastructure.configs.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener que recibe eventos de usuario desde el exchange user.exchange
 * y sincroniza (upsert) la información local para el módulo Admin.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredListener {

    private final CreateUserUseCase createUserUseCase;
    private final UserEventMapper mapper;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleUserRegistered(UserEvent event) {
        log.info("[UserRegisteredListener] Received UserEvent: {}", event);
        if (event == null || event.getUserId() == null) {
            log.warn("[UserRegisteredListener] Ignoring empty event or missing userId");
            return;
        }

        try {
            var domainUser = mapper.toDomain(event);
            createUserUseCase.createUser(domainUser);
            log.info("[UserRegisteredListener] User synced: id={}", event.getUserId());
        } catch (Exception e) {
            log.error("[UserRegisteredListener] Error syncing user id=" + event.getUserId(), e);
        }
    }
}
