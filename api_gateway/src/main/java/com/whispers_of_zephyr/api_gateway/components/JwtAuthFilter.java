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
            "/user-service/api/v1/public-key",
            "/user-service/api/v1/user/auth/reset-password/request",
            "/user-service/api/v1/user/auth/reset-password/validate-otp",
            "/user-service/api/v1/user/auth/reset-password/confirm-password").collect(Collectors.toSet());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Request to path: {}", exchange.getRequest().getURI().getPath());
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getURI().getPath();

        // If the path is public, allow request without checking JWT
        if (isPublicPath(requestPath)) {
            log.info("Path is public, proceeding without JWT check: {}", requestPath);
            return chain.filter(exchange);
        }
        log.info("Path is protected, JWT validation required: {}", requestPath);
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
                    if (!isValid) {
                        return unauthorizedResponse(exchange);
                    }
                    log.info("JWT is valid, extracting userId");
                    return jwtUtil.extractUserId(token) // Extract userId
                            .flatMap(userId -> {
                                log.info("Extracted userId: {}", userId);
                                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                        .header("X-User-Id", userId) // Attach userId to headers
                                        .build();

                                ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
                                return chain.filter(modifiedExchange);
                            });
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