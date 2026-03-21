package com.grupo51.oficinamecanica.atendimento.application.dto;

import com.grupo51.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo51.oficinamecanica.comum.validation.NoXss;
import jakarta.validation.constraints.*;

public record AberturaOSDTO(
        @NotBlank(message = "Placa do veículo é obrigatória")
        @Pattern(regexp = "^[A-Z]{3}\\d[A-Z]\\d{2}$|^[A-Z]{3}-?\\d{4}$", 
                 message = "Placa deve ser válida")
        @NoSqlInjection
        String placa,

        @NotBlank(message = "Descrição do problema é obrigatória")
        @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
        @NoSqlInjection
        @NoXss
        String descricaoProblema
) {}