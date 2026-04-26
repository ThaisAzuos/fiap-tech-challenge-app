package com.grupo37.oficinamecanica.estoque.controller;

import com.grupo37.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo37.oficinamecanica.comum.validation.NoXss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Payload para cadastro de peca")
public record PecaDTO(
        @Schema(description = "Nome da peca", example = "Filtro de Oleo Mann W712")
        @NotBlank(message = "Nome da peça é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        @NoSqlInjection
        @NoXss
        String nome,

        @Schema(description = "Preco unitario da peca", example = "48.90")
        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que 0")
        @DecimalMax(value = "999999.99", message = "Preço muito alto")
        BigDecimal preco,

        @Schema(description = "Quantidade disponivel no estoque", example = "100")
        @NotNull(message = "Quantidade em estoque é obrigatória")
        @Min(value = 0, message = "Quantidade não pode ser negativa")
        @Max(value = 999999, message = "Quantidade máxima excedida")
        Integer quantidadeEstoque
) {}
