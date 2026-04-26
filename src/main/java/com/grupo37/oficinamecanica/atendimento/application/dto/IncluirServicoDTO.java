package com.grupo37.oficinamecanica.atendimento.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Payload para incluir servico na ordem de servico")
public record IncluirServicoDTO(
        @Schema(description = "UUID do servico", example = "550e8400-e29b-41d4-a716-446655440002")
        @NotNull(message = "servicoId e obrigatorio")
        UUID servicoId
) {}
