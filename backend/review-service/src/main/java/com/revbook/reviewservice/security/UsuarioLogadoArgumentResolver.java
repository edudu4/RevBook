package com.revbook.reviewservice.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UsuarioLogadoArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    public UsuarioLogadoArgumentResolver(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UsuarioLogado.class)
                && parameter.getParameterType().equals(UsuarioAutenticado.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String header = request != null ? request.getHeader("Authorization") : null;

        if (header == null || !header.startsWith("Bearer ")) {
            throw new NaoAutenticadoException("Token ausente");
        }

        UsuarioAutenticado usuario = jwtService.validar(header.substring("Bearer ".length()));
        if (usuario == null) {
            throw new NaoAutenticadoException("Token inválido");
        }

        return usuario;
    }
}
