package com.lxp.content.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "course.exchange";
    public static final String QUEUE = "course.events";
    public static final String ROUTING_KEY = "course.#";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public TopicExchange courseExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue courseQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding courseBinding(Queue courseQueue, TopicExchange courseExchange) {
        return BindingBuilder
                .bind(courseQueue)
                .to(courseExchange)
                .with(ROUTING_KEY);
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);
        return template;
    }
}
