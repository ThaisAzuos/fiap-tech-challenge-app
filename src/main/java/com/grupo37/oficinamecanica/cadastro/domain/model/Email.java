package com.grupo37.oficinamecanica.cadastro.domain.model;

public record Email(String endereco) {
    public Email {
        if (endereco == null || !endereco.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Email deve ter formato válido");
        }
    }
}
