package com.whispers_of_zephyr.user_service.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.whispers_of_zephyr.user_service.components.CredentialsComponent;

@Configuration
public class SecurityConfig {

        @Autowired
        private final CredentialsComponent credentialsComponent;

        public SecurityConfig(CredentialsComponent credentialsComponent) {
                this.credentialsComponent = credentialsComponent;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
                UserDetails user = User.withUsername(credentialsComponent.getUsername())
                                .password(passwordEncoder.encode(credentialsComponent.getPassword()))
                                .roles("ADMIN")
                                .build();
                return new InMemoryUserDetailsManager(user);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers(request -> (request.getMethod().equals("POST")
                                                                && request.getRequestURI()
                                                                                .equals("/user-service/api/v1/user"))
                                                                ||
                                                                (request.getMethod().equals("GET")
                                                                                && request.getRequestURI().equals(
                                                                                                "/user-service/api/v1/public-key"))
                                                                ||
                                                                (request.getMethod().equals("GET")
                                                                                && request.getRequestURI().equals(
                                                                                                "/user-service/api/v1/user/login"))))
                                .authorizeHttpRequests((authz) -> authz
                                                .requestMatchers(HttpMethod.POST, "/user-service/api/v1/user")
                                                .permitAll() // Allow
                                                // POST
                                                // for
                                                // login
                                                .requestMatchers(HttpMethod.GET, "/user-service/api/v1/public-key",
                                                                "/user-service/api/v1/user/login")
                                                .permitAll() // Allow
                                                             // GET
                                                // for
                                                // public-key
                                                // and user
                                                .requestMatchers("/user-service/api/v1/users/**").hasRole("USER") // Protect
                                                                                                                  // users/**
                                                // with ROLE_USER
                                                .requestMatchers("/user-service/api/v1/admin/**").hasRole("ADMIN") // Protect
                                                // admin/** with
                                                // ROLE_ADMIN
                                                .anyRequest().authenticated()) // All other requests require
                                                                               // authentication
                                .formLogin(withDefaults())
                                .httpBasic(withDefaults());
                return http.build();
        }

}
