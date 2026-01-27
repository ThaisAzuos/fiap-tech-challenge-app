package com.grupo51.oficinamecanica.cadastro.model;

public record Email(String endereco) {
    public Email {
        if (endereco == null || !endereco.contains("@")) {
            throw new IllegalArgumentException("E-mail com formato inválido.");
        }
    }
}
