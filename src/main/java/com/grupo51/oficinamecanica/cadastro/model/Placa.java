package com.grupo51.oficinamecanica.cadastro.model;

public record Placa(String valor) {
    public Placa {
        if (valor == null || !valor.toUpperCase().matches("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$|^[A-Z]{3}-[0-9]{4}$")) {
            throw new IllegalArgumentException("Placa inválida. Use o padrão Mercosul (ABC1D23) ou antigo (ABC1234).");
        }
    }
}
