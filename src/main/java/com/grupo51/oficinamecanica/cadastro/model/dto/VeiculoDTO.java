package com.grupo51.oficinamecanica.cadastro.model.dto;

public record VeiculoDTO(
        String placa,
        String modelo,
        String marca,
        Integer ano,
        String cor,
        String cpfDono
) {}
