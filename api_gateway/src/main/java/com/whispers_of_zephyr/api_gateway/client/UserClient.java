package com.whispers_of_zephyr.api_gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;

import com.whispers_of_zephyr.api_gateway.config.FeignClientConfig;

@FeignClient(name = "user-service", url = "${application.config.user-service.url}", configuration = FeignClientConfig.class)
@Lazy
public interface UserClient {

    @GetMapping(value = "/public-key", produces = "text/plain")
    String getPublicKey();
}
