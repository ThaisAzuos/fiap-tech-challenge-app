package com.grupo51.oficinamecanica.atendimento.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrdemServicoListDTO(
        UUID id,
        String status,
        LocalDateTime dataAbertura,
        BigDecimal valorTotal,
        String placaVeiculo,
        String modeloVeiculo,
        String nomeCliente
) {}
