package com.whispers_of_zephyr.api_gateway.components;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.whispers_of_zephyr.api_gateway.utils.JwtUtil;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class JwtAuthFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final Set<String> PUBLIC_PATHS = Stream.of(
            "/user-service/api/v1/user/login",
            "/user-service/api/v1/user",
            "/user-service/api/v1/public-key").collect(Collectors.toSet());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Request to path: {}", exchange.getRequest().getURI().getPath());
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getURI().getPath();

        // If the path is public, allow request without checking JWT
        if (isPublicPath(requestPath)) {
            return chain.filter(exchange);
        }

        // Check if the request has an Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Authorization header: {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange);
        }

        // Extract JWT token
        String token = authHeader.substring(7);

        // Validate JWT reactively
        return jwtUtil.validateToken(token)
                .flatMap(isValid -> {
                    if (isValid) {
                        return chain.filter(exchange);
                    } else {
                        return unauthorizedResponse(exchange);
                    }
                });
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
