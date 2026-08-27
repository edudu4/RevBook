package com.revbook.gateway.config;

import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Só decide QUEM pode chamar cada rota — a validação da assinatura do JWT vem pronta do
 * OAuth2 Resource Server (configurado em application.yml), e o token é repassado sem
 * alteração para o serviço de destino, que extrai o usuário autenticado por conta própria.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String jwtSecret;

    public SecurityConfig(@Value("${revbook.jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/reviews", "/reviews/rate").authenticated()
                        .pathMatchers(HttpMethod.POST, "/reviews/*/comments").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/comments/*").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/comments/*").authenticated()
                        .pathMatchers(HttpMethod.POST, "/comments/*/reactions").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/reactions/*").authenticated()
                        .anyExchange().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                }));

        return http.build();
    }
}
