package edu.eci.ATENEA_Administration_BackEnd.infrastructure.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración RabbitMQ para RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "admin.exchange";
    public static final String SUSPEND_ROUTING = "admin.user.suspended";
    public static final String VALIDATION_ROUTING = "admin.validation.approved";
    public static final String DRIVER_DOCUMENT_ROUTING = "admin.driver.document.uploaded";

    public static final String SUSPEND_QUEUE = "admin.suspend.queue";
    public static final String VALIDATION_QUEUE = "admin.validation.queue";
    public static final String DRIVER_DOCUMENT_QUEUE = "admin.driver.document.queue";

    /**
     * Configura el exchange de administración
     *
     * @return TopicExchange configurado
     */
    @Bean
    public TopicExchange adminExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }


    /**
     * Configura la cola de suspensión
     *
     * @return Queue configurada
     */
    @Bean
    public Queue suspendQueue() { return QueueBuilder.durable(SUSPEND_QUEUE).build(); }

    /**
     * Configura la cola de validación
     *
     * @return Queue configurada
     */
    @Bean
    public Queue validationQueue() { return QueueBuilder.durable(VALIDATION_QUEUE).build(); }

    /**
     * Configura la cola de documentos de conductor
     *
     * @return Queue configurada
     */
    @Bean
    public Queue driverDocumentQueue() { return QueueBuilder.durable(DRIVER_DOCUMENT_QUEUE).build(); }

    /**
     * Configura el binding para suspensión
     *
     * @param suspendQueue Cola de suspensión
     * @param adminExchange Exchange de administración
     * @return Binding configurado
     */
    @Bean
    public Binding bindSuspend(Queue suspendQueue, TopicExchange adminExchange) {
        return BindingBuilder.bind(suspendQueue).to(adminExchange).with(SUSPEND_ROUTING);
    }


    /**
     * Configura el binding para validación
     *
     * @param validationQueue Cola de validación
     * @param adminExchange Exchange de administración
     * @return Binding configurado
     */
    @Bean
    public Binding bindValidation(Queue validationQueue, TopicExchange adminExchange) {
        return BindingBuilder.bind(validationQueue).to(adminExchange).with(VALIDATION_ROUTING);
    }

    /**
     * Configura el binding para documentos de conductor
     *
     * @param driverDocumentQueue Cola de documentos
     * @param adminExchange Exchange de administración
     * @return Binding configurado
     */
    @Bean
    public Binding bindDriverDocument(Queue driverDocumentQueue, TopicExchange adminExchange) {
        return BindingBuilder.bind(driverDocumentQueue).to(adminExchange).with(DRIVER_DOCUMENT_ROUTING);
    }

    /**
     * Configura el convertidor JSON para RabbitMQ
     *
     * @return Jackson2JsonMessageConverter configurado
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    /**
     * Configura el template de RabbitMQ
     *
     * @param cf ConnectionFactory
     * @param conv Convertidor de mensajes
     * @return RabbitTemplate configurado
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter conv) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(conv);
        return t;
    }


    /**
     * Configura el factory para listeners RabbitMQ
     *
     * @param cf ConnectionFactory
     * @param conv Convertidor de mensajes
     * @return SimpleRabbitListenerContainerFactory configurado
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf,
                                                                               Jackson2JsonMessageConverter conv) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(conv);
        f.setConcurrentConsumers(2);
        f.setMaxConcurrentConsumers(10);
        return f;
    }
}
