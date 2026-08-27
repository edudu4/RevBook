package com.revbook.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * auth-service e review-service leem o usuário logado a partir do header Authorization.
 * Como o token agora chega do navegador via cookie httpOnly, este filtro o copia para o
 * header antes de encaminhar a requisição, mantendo a validação interna dos serviços igual.
 */
@Component
public class CookieToAuthorizationHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return chain.filter(exchange);
        }

        HttpCookie cookie = request.getCookies().getFirst(CookieBearerTokenConverter.COOKIE_NOME);
        if (cookie == null || cookie.getValue().isBlank()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest mutado = request.mutate()
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + cookie.getValue()))
                .build();
        return chain.filter(exchange.mutate().request(mutado).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
