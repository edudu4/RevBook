package com.revbook.gateway.config;

import com.revbook.gateway.security.CookieBearerTokenConverter;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.access.server.BearerTokenServerAccessDeniedHandler;
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;

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
        BearerTokenServerAuthenticationEntryPoint entryPointPadrao = new BearerTokenServerAuthenticationEntryPoint();
        BearerTokenServerAccessDeniedHandler accessDeniedPadrao = new BearerTokenServerAccessDeniedHandler();

        ServerAuthenticationEntryPoint entryPoint = (exchange, ex) -> {
            adicionarHeadersCors(exchange);
            return entryPointPadrao.commence(exchange, ex);
        };

        ServerAccessDeniedHandler accessDeniedHandler = (exchange, ex) -> {
            adicionarHeadersCors(exchange);
            return accessDeniedPadrao.handle(exchange, ex);
        };

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
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenConverter(cookieBearerTokenConverter)
                        .jwt(jwt -> {
                        }));

        return http.build();
    }

    private void adicionarHeadersCors(ServerWebExchange exchange) {
        String origem = exchange.getRequest().getHeaders().getOrigin();
        if (origem == null) {
            return;
        }
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origem);
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.VARY, HttpHeaders.ORIGIN);
    }
}
