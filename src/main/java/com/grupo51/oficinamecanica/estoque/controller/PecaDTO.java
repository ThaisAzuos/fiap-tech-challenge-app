package com.grupo51.oficinamecanica.estoque.controller;

import java.math.BigDecimal;

public record PecaDTO(
        String nome,
        BigDecimal preco,
        Integer quantidadeEstoque
) {}
