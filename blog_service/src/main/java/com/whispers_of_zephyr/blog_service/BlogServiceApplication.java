package com.whispers_of_zephyr.blog_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.whispers_of_zephyr.blog_service.client")
// @EnableConfigurationProperties(MyAppConfig.class) -> This is not necessary,
// as we are getting the value from the MyAppConfigComponent
public class BlogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogServiceApplication.class, args);
	}

}
