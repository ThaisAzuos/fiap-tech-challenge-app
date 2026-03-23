package com.grupo51.oficinamecanica.atendimento.application.dto;

import com.grupo51.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo51.oficinamecanica.comum.validation.NoXss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Payload para abertura de ordem de servico")
public record AberturaOSDTO(
        @Schema(description = "Placa do veiculo", example = "ABC1D23")
        @NotBlank(message = "Placa do veículo é obrigatória")
        @Pattern(regexp = "^[A-Z]{3}\\d[A-Z]\\d{2}$|^[A-Z]{3}-?\\d{4}$", 
                 message = "Placa deve ser válida")
        @NoSqlInjection
        String placa,

        @Schema(description = "Relato inicial do problema", example = "Barulho na transmissao ao acelerar. Falha intermitente.")
        @NotBlank(message = "Descrição do problema é obrigatória")
        @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
        @NoSqlInjection
        @NoXss
        String descricaoProblema
) {}