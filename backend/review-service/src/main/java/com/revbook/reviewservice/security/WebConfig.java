package com.revbook.reviewservice.security;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UsuarioLogadoArgumentResolver usuarioLogadoArgumentResolver;

    public WebConfig(UsuarioLogadoArgumentResolver usuarioLogadoArgumentResolver) {
        this.usuarioLogadoArgumentResolver = usuarioLogadoArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(usuarioLogadoArgumentResolver);
    }
}
