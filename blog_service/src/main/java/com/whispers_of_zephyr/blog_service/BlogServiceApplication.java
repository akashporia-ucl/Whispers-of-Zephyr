package com.whispers_of_zephyr.blog_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.whispers_of_zephyr.blog_service.configurations.MyAppConfig;

@SpringBootApplication
@EnableConfigurationProperties(MyAppConfig.class)
public class BlogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogServiceApplication.class, args);
	}

}
