package com.whispers_of_zephyr.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public Queue userCreatedQueue() {
        return new Queue("user.created.queue", true);
    }

    @Bean
    public TopicExchange blogExchange() {
        return new TopicExchange("blogExchange");
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("userExchange");
    }

    @Bean
    public Binding blogCreatedBinding(Queue blogCreatedQueue, TopicExchange blogExchange) {
        return BindingBuilder.bind(blogCreatedQueue).to(blogExchange).with("blog.created");
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userCreatedQueue).to(userExchange).with("user.created");
    }

}
