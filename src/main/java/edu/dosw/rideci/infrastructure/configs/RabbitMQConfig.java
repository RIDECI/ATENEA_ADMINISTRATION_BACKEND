package edu.dosw.rideci.infrastructure.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración RabbitMQ para RideECI
 *
 * @author RideECI
 * @version 1.0
 */
@Configuration
public class RabbitMQConfig {

    // Admin
    public static final String EXCHANGE = "admin.exchange";
    public static final String SUSPEND_ROUTING = "admin.user.suspended";
    public static final String VALIDATION_ROUTING = "admin.validation.approved";
    public static final String DRIVER_DOCUMENT_ROUTING = "admin.driver.document.uploaded";

    public static final String SUSPEND_QUEUE = "admin.suspend.queue";
    public static final String VALIDATION_QUEUE = "admin.validation.queue";
    public static final String DRIVER_DOCUMENT_QUEUE = "admin.driver.document.queue";

    // Viajes
    public static final String TRIP_EXCHANGE = "travel.exchange";
    public static final String TRIP_CREATED_ROUTING_KEY = "travel.created";
    public static final String TRIP_CREATED_QUEUE = "travel.created.queue";

    public static final String TRIP_FINISHED_ROUTING_KEY = "travel.completed";
    public static final String TRIP_FINISHED_QUEUE = "travel.completed.queue";

    // Usuario
    public static final String EXCHANGE_USER = "user.exchange";
    public static final String USER_CREATED_QUEUE = "user.sync.queue";
    public static final String USER_ROUTING_KEY = "user.#";

    //Perfiles
    public static final String PROFILE_CREATED_QUEUE = "profile.sync.queue";
    public static final String RATING_CREATED_QUEUE = "rating.sync.queue";
    public static final String EXCHANGE_PROFILE = "profile.exchange";
    public static final String PROFILE_CREATED_ROUTING_KEY = "profile.created";
    public static final String RATING_CREATED_ROUTING_KEY = "rating.created";



    //-----Usuarios-----

    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(USER_CREATED_QUEUE).build();
    }

    @Bean
    public TopicExchange userExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_USER).durable(true).build();
    }

    @Bean
    public Binding bindUserCreated(Queue userCreatedQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userCreatedQueue).to(userExchange).with(USER_ROUTING_KEY);
    }

    //-----Perfiles-----

    @Bean
    public Queue profileSyncQueue() {
        return QueueBuilder.durable(PROFILE_CREATED_QUEUE).build();
    }

    @Bean
    public Queue ratingSyncQueue() {
        return QueueBuilder.durable(RATING_CREATED_QUEUE).build();
    }

    @Bean
    public TopicExchange profileExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_PROFILE).durable(true).build();
    }

    @Bean
    public Binding bindProfileCreated(@Qualifier("profileSyncQueue") Queue q,
                                      @Qualifier("profileExchange") TopicExchange ex) {
        return BindingBuilder.bind(q).to(ex).with(PROFILE_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding bindRatingCreatedToProfile(@Qualifier("ratingSyncQueue") Queue q,
                                              @Qualifier("profileExchange") TopicExchange ex) {
        return BindingBuilder.bind(q).to(ex).with(RATING_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding bindRatingToTravel(@Qualifier("ratingSyncQueue") Queue q,
                                      @Qualifier("tripExchange") TopicExchange tripExchange) {
        return BindingBuilder.bind(q).to(tripExchange).with(RabbitMQConfig.TRIP_FINISHED_ROUTING_KEY);
    }

    //-----Viajes-----
    @Bean
    public TopicExchange tripExchange() {
        return ExchangeBuilder.topicExchange(TRIP_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue tripCreatedQueue() {
        return QueueBuilder.durable(TRIP_CREATED_QUEUE).build();
    }

    @Bean
    public Queue tripFinishedQueue() {
        return QueueBuilder.durable(TRIP_FINISHED_QUEUE).build();
    }

    @Bean
    public Binding tripCreatedBinding(@Qualifier("tripCreatedQueue") Queue tripCreatedQueue,
                                      @Qualifier("tripExchange") TopicExchange tripExchange) {
        return BindingBuilder.bind(tripCreatedQueue).to(tripExchange).with(TRIP_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding tripFinishedBinding(@Qualifier("tripFinishedQueue") Queue tripFinishedQueue,
                                       @Qualifier("tripExchange") TopicExchange tripExchange) {
        return BindingBuilder.bind(tripFinishedQueue).to(tripExchange).with(TRIP_FINISHED_ROUTING_KEY);
    }

    //-----Admin-----
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
    public Queue suspendQueue() {
        return QueueBuilder.durable(SUSPEND_QUEUE).build();
    }

    /**
     * Configura la cola de validación
     *
     * @return Queue configurada
     */
    @Bean
    public Queue validationQueue() {
        return QueueBuilder.durable(VALIDATION_QUEUE).build();
    }

    /**
     * Configura la cola de documentos de conductor
     *
     * @return Queue configurada
     */
    @Bean
    public Queue driverDocumentQueue() {
        return QueueBuilder.durable(DRIVER_DOCUMENT_QUEUE).build();
    }

    /**
     * Configura el binding para suspensión
     *
     * @param suspendQueue Cola de suspensión
     * @param adminExchange Exchange de administración
     * @return Binding configurado
     */
    @Bean
    public Binding bindSuspend(@Qualifier("suspendQueue") Queue suspendQueue,
                               @Qualifier("adminExchange") TopicExchange adminExchange) {
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
    public Binding bindValidation(@Qualifier("validationQueue") Queue validationQueue,
                                  @Qualifier("adminExchange") TopicExchange adminExchange) {
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
    public Binding bindDriverDocument(@Qualifier("driverDocumentQueue") Queue driverDocumentQueue,
                                      @Qualifier("adminExchange") TopicExchange adminExchange) {
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