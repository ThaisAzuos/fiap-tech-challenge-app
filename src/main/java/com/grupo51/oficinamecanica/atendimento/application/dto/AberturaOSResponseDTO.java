package com.grupo51.oficinamecanica.atendimento.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para a abertura de Ordem de Serviço.
 * Evita problemas de LazyInitializationException ao retornar a entidade
 * OrdemServico diretamente (open-in-view=false + coleções LAZY).
 */
public record AberturaOSResponseDTO(
        UUID id,
        String placa,
        String status,
        LocalDateTime dataAbertura
) {}

