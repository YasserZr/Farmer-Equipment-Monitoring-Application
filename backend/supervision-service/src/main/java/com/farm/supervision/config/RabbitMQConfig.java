package com.farm.supervision.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for consuming equipment events.
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.exchange.name:equipment-events}")
    private String exchangeName;
    
    @Value("${rabbitmq.queue.supervision:supervision-events-queue}")
    private String supervisionQueueName;
    
    @Value("${rabbitmq.routing.key:equipment.#}")
    private String routingKey;
    
    /**
     * Reference to the equipment events exchange (should already exist)
     */
    @Bean
    public TopicExchange equipmentExchange() {
        return new TopicExchange(exchangeName);
    }
    
    /**
     * Create supervision queue for consuming events
     */
    @Bean
    public Queue supervisionQueue() {
        return QueueBuilder.durable(supervisionQueueName)
                .withArgument("x-message-ttl", 86400000) // 24 hours TTL
                .withArgument("x-dead-letter-exchange", "dlx-equipment-events")
                .build();
    }
    
    /**
     * Dead letter exchange for failed messages
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx-equipment-events");
    }
    
    /**
     * Dead letter queue
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("dlq-equipment-events").build();
    }
    
    /**
     * Bind DLQ to DLX
     */
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("dlq");
    }
    
    /**
     * Bind supervision queue to equipment exchange
     */
    @Bean
    public Binding supervisionBinding(Queue supervisionQueue, TopicExchange equipmentExchange) {
        return BindingBuilder
                .bind(supervisionQueue)
                .to(equipmentExchange)
                .with(routingKey);
    }
    
    /**
     * JSON message converter
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * Rabbit listener container factory with error handling
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setDefaultRequeueRejected(false); // Don't requeue failed messages
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
