package com.revbook.gateway.config;

import com.revbook.gateway.security.CookieBearerTokenConverter;
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

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String jwtSecret;
    private final CookieBearerTokenConverter cookieBearerTokenConverter;

    public SecurityConfig(
            @Value("${revbook.jwt.secret}") String jwtSecret, CookieBearerTokenConverter cookieBearerTokenConverter) {
        this.jwtSecret = jwtSecret;
        this.cookieBearerTokenConverter = cookieBearerTokenConverter;
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
                        .pathMatchers(HttpMethod.PUT, "/reviews/*").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/reviews/*").authenticated()
                        .pathMatchers(HttpMethod.POST, "/reviews/*/comments").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/comments/*").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/comments/*").authenticated()
                        .pathMatchers(HttpMethod.POST, "/comments/*/reactions").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/reactions/*").authenticated()
                        .pathMatchers("/notifications/**").authenticated()
                        .anyExchange().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenConverter(cookieBearerTokenConverter)
                        .jwt(jwt -> {
                        }));

        return http.build();
    }
}
