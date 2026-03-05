package com.example.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers(HttpMethod.GET, "/employee/**", "/department/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.POST, "/employee/**", "/department/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/employee/**", "/department/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/employee/**", "/department/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/employee/**", "/department/**").hasRole("ADMIN")
                        .anyExchange().denyAll()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) -> writeError(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "AUTH-401", "Authentication required or invalid token"))
                        .accessDeniedHandler((exchange, ex) -> writeError(exchange.getResponse(), HttpStatus.FORBIDDEN, "AUTH-403", "Access denied"))
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private Mono<Void> writeError(org.springframework.http.server.reactive.ServerHttpResponse response,
                                  HttpStatus status,
                                  String code,
                                  String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] payload = objectMapper.writeValueAsBytes(new AuthErrorResponse(code, message));
            return response.writeWith(Mono.just(response.bufferFactory().wrap(payload)));
        } catch (JsonProcessingException e) {
            byte[] fallback = "{\"code\":\"AUTH-500\",\"message\":\"Authentication error\"}".getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(fallback)));
        }
    }
}
