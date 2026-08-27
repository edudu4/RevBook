package com.revbook.reviewservice.controller;

import com.revbook.reviewservice.dto.UserStatsResponse;
import com.revbook.reviewservice.service.EstatisticasService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstatisticasController {

    private final EstatisticasService estatisticasService;

    public EstatisticasController(EstatisticasService estatisticasService) {
        this.estatisticasService = estatisticasService;
    }

    @GetMapping("/users/{userId}/profile")
    public UserStatsResponse calcular(@PathVariable Long userId) {
        return estatisticasService.calcular(userId);
    }
}
