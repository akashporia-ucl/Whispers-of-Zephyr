package com.whispers_of_zephyr.blog_service.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyAppConfigComponent {

    @Value("${custom.image.path}")
    private String defaultImagePath;

    public String getDefaultImagePath() {
        return defaultImagePath;
    }

}
