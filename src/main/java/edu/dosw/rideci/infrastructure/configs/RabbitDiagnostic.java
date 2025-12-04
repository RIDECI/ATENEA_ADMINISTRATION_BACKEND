package edu.dosw.rideci.infrastructure.configs;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitDiagnostic {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitDiagnostic.class);
    private final RabbitAdmin admin;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        boolean createdQueue = admin.getQueueProperties("travel.created.queue") != null;
        boolean completedQueue = admin.getQueueProperties("travel.completed.queue") != null;

        LOG.info("queue travel.created.queue exists? {}", createdQueue);
        LOG.info("queue travel.completed.queue exists? {}", completedQueue);
        LOG.info("inferred exchange travel.exchange present? {}", (createdQueue || completedQueue));
    }
}
