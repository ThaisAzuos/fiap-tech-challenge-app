package com.grupo37.oficinamecanica.atendimento.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Payload para incluir peca na ordem de servico")
public record IncluirPecaDTO(
        @Schema(description = "UUID da peca cadastrada no estoque", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotNull(message = "pecaId e obrigatorio")
        UUID pecaId,

        @Schema(description = "Quantidade de itens da peca", example = "1")
        @NotNull(message = "quantidade e obrigatoria")
        @Min(value = 1, message = "quantidade deve ser maior que zero")
        Integer quantidade
) {}
