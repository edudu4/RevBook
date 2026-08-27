package com.revbook.reviewservice.controller;

import com.revbook.reviewservice.dto.NotificationResponse;
import com.revbook.reviewservice.security.UsuarioAutenticado;
import com.revbook.reviewservice.security.UsuarioLogado;
import com.revbook.reviewservice.service.NotificacaoService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/notifications")
    public List<NotificationResponse> listar(@UsuarioLogado UsuarioAutenticado usuario) {
        return notificacaoService.listarPorUsuario(usuario.id()).stream().map(NotificationResponse::de).toList();
    }

    @GetMapping("/notifications/unread-count")
    public Map<String, Long> contarNaoLidas(@UsuarioLogado UsuarioAutenticado usuario) {
        return Map.of("count", notificacaoService.contarNaoLidas(usuario.id()));
    }

    @PostMapping("/notifications/{id}/read")
    public void marcarComoLida(@PathVariable Long id, @UsuarioLogado UsuarioAutenticado usuario) {
        notificacaoService.marcarComoLida(id, usuario.id());
    }

    @PostMapping("/notifications/read-all")
    public void marcarTodasComoLidas(@UsuarioLogado UsuarioAutenticado usuario) {
        notificacaoService.marcarTodasComoLidas(usuario.id());
    }
}
