package com.grupo37.oficinamecanica.cadastro.domain.model;

public record Placa(String valor) {
    public Placa {
        if (valor == null || !valor.matches("^[A-Z]{3}\\d[A-Z]\\d{2}$|^[A-Z]{3}-?\\d{4}$")) {
            throw new IllegalArgumentException("Placa deve ter formato válido (ex: ABC1D23 ou ABC-1234)");
        }
    }
}
