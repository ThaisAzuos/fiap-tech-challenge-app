package com.grupo37.oficinamecanica.cadastro.domain.model;

public record Email(String endereco) {
    public Email {
        if (endereco == null || !endereco.contains("@")) {
            throw new IllegalArgumentException("E-mail com formato inválido.");
        }
    }
}
