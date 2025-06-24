package com.whispers_of_zephyr.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue("notification-service.notify.updates", true);
    }

    @Bean
    public Queue createBlogQueue() {
        return new Queue("blog-service.blog.create", true);
    }

    @Bean
    public Queue createBlogDLQueue() {
        return new Queue("blog-service.blog.create.dlq", true);
    }

    @Bean
    public Queue deleteBlogQueue() {
        return new Queue("blog-service.blog.delete", true);
    }

    @Bean
    public Queue deleteBlogDLQueue() {
        return new Queue("blog-service.blog.delete.dlq", true);
    }

    @Bean
    public TopicExchange blogExchange() {
        return new TopicExchange("blogExchange");
    }

    @Bean
    public TopicExchange notifyExchange() {
        return new TopicExchange("notifyExchange");
    }

    @Bean
    public TopicExchange blogDeadLetterExchange() {
        return new TopicExchange("blogDeadLetterExchange");
    }

    @Bean
    public Binding blogCreateBinding(Queue createBlogQueue, TopicExchange blogExchange) {
        return BindingBuilder.bind(createBlogQueue).to(blogExchange).with("blog-service.blog.create");
    }

    @Bean
    public Binding blogDeleteBinding(Queue deleteBlogQueue, TopicExchange blogExchange) {
        return BindingBuilder.bind(deleteBlogQueue).to(blogExchange).with("blog-service.blog.delete");
    }

    @Bean
    public Binding blogCreateDLQBinding(Queue createBlogDLQueue, TopicExchange blogDeadLetterExchange) {
        return BindingBuilder.bind(createBlogDLQueue).to(blogDeadLetterExchange).with("blog-service.blog.create.dlq");
    }

    @Bean
    public Binding blogDeleteDLQBinding(Queue deleteBlogDLQueue, TopicExchange blogDeadLetterExchange) {
        return BindingBuilder.bind(deleteBlogDLQueue).to(blogDeadLetterExchange).with("blog-service.blog.delete.dlq");
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange notifyExchange) {
        return BindingBuilder.bind(notificationQueue).to(notifyExchange).with("notification-service.notify.updates");
    }
}
