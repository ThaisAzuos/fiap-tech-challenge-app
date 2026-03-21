package com.grupo51.oficinamecanica.agendamento.controller.dto;

import com.grupo51.oficinamecanica.agendamento.model.TipoAgendamento;
import com.grupo51.oficinamecanica.comum.validation.NoSqlInjection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Schema(description = "Payload para criacao de agendamento")
public record AgendamentoRequestDTO(
        @Schema(description = "CPF do cliente", example = "73383053036")
        @NotBlank(message = "ID do cliente é obrigatório")
        @NoSqlInjection
        String clienteId,

        @Schema(description = "Placa do veiculo", example = "ABC1D23")
        @NotBlank(message = "ID do veículo é obrigatório")
        @NoSqlInjection
        String veiculoId,

        @Schema(description = "CPF do mecanico/recurso", example = "09151522037")
        @NotBlank(message = "ID do recurso é obrigatório")
        @NoSqlInjection
        String recursoId,

        @Schema(description = "Inicio previsto", example = "2026-06-10T09:00:00")
        @NotNull(message = "Data/hora de início é obrigatória")
        @FutureOrPresent(message = "Data/hora de início deve ser no futuro ou presente")
        LocalDateTime dataHoraInicio,

        @Schema(description = "Fim previsto", example = "2026-06-10T11:00:00")
        @NotNull(message = "Data/hora de fim é obrigatória")
        @Future(message = "Data/hora de fim deve ser no futuro")
        LocalDateTime dataHoraFim,

        @Schema(description = "Tipo do agendamento", allowableValues = {"ANALISE", "EXECUCAO"}, example = "ANALISE")
        @NotNull(message = "Tipo de agendamento é obrigatório")
        TipoAgendamento tipo
) {}
