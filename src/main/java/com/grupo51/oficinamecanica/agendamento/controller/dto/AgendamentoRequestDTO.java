package com.grupo51.oficinamecanica.agendamento.controller.dto;

import com.grupo51.oficinamecanica.agendamento.model.TipoAgendamento;
import com.grupo51.oficinamecanica.comum.validation.NoSqlInjection;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoRequestDTO(
        @NotBlank(message = "ID do cliente é obrigatório")
        @NoSqlInjection
        String clienteId,

        @NotBlank(message = "ID do veículo é obrigatório")
        @NoSqlInjection
        String veiculoId,

        @NotBlank(message = "ID do recurso é obrigatório")
        @NoSqlInjection
        String recursoId,

        @NotNull(message = "Data/hora de início é obrigatória")
        @FutureOrPresent(message = "Data/hora de início deve ser no futuro ou presente")
        LocalDateTime dataHoraInicio,

        @NotNull(message = "Data/hora de fim é obrigatória")
        @Future(message = "Data/hora de fim deve ser no futuro")
        LocalDateTime dataHoraFim,

        @NotNull(message = "Tipo de agendamento é obrigatório")
        TipoAgendamento tipo
) {}
