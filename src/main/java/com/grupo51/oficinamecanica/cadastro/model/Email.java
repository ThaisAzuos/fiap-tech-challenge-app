package com.grupo51.oficinamecanica.cadastro.model;

public record Email(String email) {
    public Email {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("E-mail com formato inválido.");
        }
    }
}
