package com.grupo51.oficinamecanica.agendamento.controller.dto;

import com.grupo51.oficinamecanica.agendamento.model.TipoAgendamento;
import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoRequestDTO(
        String clienteId,
        String veiculoId,
        String recursoId,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        TipoAgendamento tipo
) {}
