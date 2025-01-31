package com.whispers_of_zephyr.api_gateway.config;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.optionals.OptionalDecoder;

@Configuration
public class FeignClientConfig {

    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters(
                new StringHttpMessageConverter(StandardCharsets.UTF_8), // Allows Feign to decode `text/plain`
                new MappingJackson2HttpMessageConverter() // Allows JSON decoding
        );
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(() -> httpMessageConverters());
    }

    @Bean
    public Decoder feignDecoder() {
        return new OptionalDecoder(new SpringDecoder(() -> httpMessageConverters()));
    }
}
