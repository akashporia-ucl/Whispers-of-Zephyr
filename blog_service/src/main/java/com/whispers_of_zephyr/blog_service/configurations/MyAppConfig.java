package com.whispers_of_zephyr.blog_service.configurations;

import org.springframework.context.annotation.Configuration;

@Configuration
// @ConfigurationProperties(prefix = "custom.image") -> uncomment this line when
// you uncomment the @EnableConfigurationProperties(MyAppConfig class)
public class MyAppConfig {

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
