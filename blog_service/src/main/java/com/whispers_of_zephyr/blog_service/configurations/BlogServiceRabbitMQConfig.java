package com.whispers_of_zephyr.blog_service.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.whispers_of_zephyr.common.config.RabbitMQConfig;

@Configuration
@Import(RabbitMQConfig.class)
public class BlogServiceRabbitMQConfig {
    // This class imports the RabbitMQConfig from the common library
    // All beans defined in the common RabbitMQConfig will be available here
}