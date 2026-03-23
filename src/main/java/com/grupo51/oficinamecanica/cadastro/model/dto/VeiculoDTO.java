package com.grupo51.oficinamecanica.cadastro.model.dto;

import com.grupo51.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo51.oficinamecanica.comum.validation.NoXss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Payload para cadastro de veiculo")
public record VeiculoDTO(
        @Schema(description = "Placa no formato Mercosul ou tradicional", example = "DEF4G56")
        @NotBlank(message = "Placa é obrigatória")
        @Pattern(regexp = "^[A-Z]{3}\\d[A-Z]\\d{2}$|^[A-Z]{3}-?\\d{4}$", 
                 message = "Placa deve ser válida (padrão Mercosul ou tradicional)")
        @NoSqlInjection
        String placa,

        @Schema(description = "Modelo do veiculo", example = "Corolla")
        @NotBlank(message = "Modelo é obrigatório")
        @Size(max = 50, message = "Modelo deve ter no máximo 50 caracteres")
        @NoSqlInjection
        @NoXss
        String modelo,

        @Schema(description = "Marca do veiculo", example = "Toyota")
        @NotBlank(message = "Marca é obrigatória")
        @Size(max = 50, message = "Marca deve ter no máximo 50 caracteres")
        @NoSqlInjection
        @NoXss
        String marca,

        @Schema(description = "Ano de fabricacao", example = "2023")
        @NotNull(message = "Ano é obrigatório")
        @Min(value = 1900, message = "Ano deve ser maior que 1900")
        @Max(value = 2100, message = "Ano deve ser menor que 2100")
        Integer ano,

        @Schema(description = "Cor predominante", example = "Preto")
        @NotBlank(message = "Cor é obrigatória")
        @Size(max = 30, message = "Cor deve ter no máximo 30 caracteres")
        @NoSqlInjection
        @NoXss
        String cor,

        @Schema(description = "CPF do proprietario sem pontuacao", example = "73383053036")
        @NotBlank(message = "CPF do dono é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos")
        @NoSqlInjection
        String cpfDono
) {}
