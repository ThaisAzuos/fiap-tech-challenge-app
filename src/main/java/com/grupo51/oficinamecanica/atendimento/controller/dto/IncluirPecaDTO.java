package com.grupo51.oficinamecanica.atendimento.controller.dto;

import java.util.UUID;

public record IncluirPecaDTO(
        UUID pecaId,
        Integer quantidade
) {}
