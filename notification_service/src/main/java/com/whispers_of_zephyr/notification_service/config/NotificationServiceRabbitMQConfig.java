package com.whispers_of_zephyr.notification_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.whispers_of_zephyr.common.config.RabbitMQConfig;

@Configuration
@Import(com.whispers_of_zephyr.common.config.RabbitMQConfig.class)
public class NotificationServiceRabbitMQConfig {
    // This class imports the RabbitMQConfig from the common library
    // All beans defined in the common RabbitMQConfig will be available here
}
