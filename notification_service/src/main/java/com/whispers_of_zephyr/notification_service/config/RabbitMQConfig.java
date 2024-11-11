package com.whispers_of_zephyr.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue blogCreatedQueue() {
        return new Queue("blog.created.queue", true);
    }

    @Bean
    public TopicExchange blogExchange() {
        return new TopicExchange("blogExchange");
    }

    @Bean
    public Binding blogCreatedBinding(Queue blogCreatedQueue, TopicExchange blogExchange) {
        return BindingBuilder.bind(blogCreatedQueue).to(blogExchange).with("blog.created");
    }

}
