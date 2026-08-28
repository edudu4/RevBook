package com.revbook.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

        HttpHeaders novosHeaders = new HttpHeaders();
        novosHeaders.putAll(request.getHeaders());
        novosHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + cookie.getValue());

        ServerHttpRequest requestDecorado = new ServerHttpRequestDecorator(request) {
            @Override
            public HttpHeaders getHeaders() {
                return novosHeaders;
            }
        };

        return chain.filter(exchange.mutate().request(requestDecorado).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
