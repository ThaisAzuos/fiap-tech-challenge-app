package com.grupo37.oficinamecanica.cadastro.domain.model;

public record Cpf(String numero) {
    public Cpf {
        if (numero == null || !numero.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF deve ter exatamente 11 dígitos numéricos");
        }
    }
}
