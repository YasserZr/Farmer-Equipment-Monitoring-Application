package com.farm.equipment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for equipment events.
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.exchange.name:equipment-events}")
    private String exchangeName;
    
    @Value("${rabbitmq.queue.name:equipment-events-queue}")
    private String queueName;
    
    @Value("${rabbitmq.routing.key:equipment.#}")
    private String routingKey;
    
    /**
     * Create the equipment events exchange
     */
    @Bean
    public TopicExchange equipmentExchange() {
        return new TopicExchange(exchangeName);
    }
    
    /**
     * Create the equipment events queue
     */
    @Bean
    public Queue equipmentQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-message-ttl", 86400000) // 24 hours TTL
                .build();
    }
    
    /**
     * Bind queue to exchange with routing key
     */
    @Bean
    public Binding equipmentBinding(Queue equipmentQueue, TopicExchange equipmentExchange) {
        return BindingBuilder
                .bind(equipmentQueue)
                .to(equipmentExchange)
                .with(routingKey);
    }
    
    /**
     * JSON message converter for RabbitMQ
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitTemplate with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
