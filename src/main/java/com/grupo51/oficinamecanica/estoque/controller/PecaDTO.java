package com.grupo51.oficinamecanica.estoque.controller;

import com.grupo51.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo51.oficinamecanica.comum.validation.NoXss;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PecaDTO(
        @NotBlank(message = "Nome da peça é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        @NoSqlInjection
        @NoXss
        String nome,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que 0")
        @DecimalMax(value = "999999.99", message = "Preço muito alto")
        BigDecimal preco,

        @NotNull(message = "Quantidade em estoque é obrigatória")
        @Min(value = 0, message = "Quantidade não pode ser negativa")
        @Max(value = 999999, message = "Quantidade máxima excedida")
        Integer quantidadeEstoque
) {}
