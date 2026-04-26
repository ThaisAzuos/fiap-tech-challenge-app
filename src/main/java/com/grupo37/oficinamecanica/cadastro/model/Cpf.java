package com.grupo37.oficinamecanica.cadastro.model;

public record Cpf(String numero) {
    public Cpf {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser vazio");
        }
        
        // Sanitização: remove tudo que não é dígito
        String limpo = numero.replaceAll("\\D", "");
        
        if (limpo.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos: " + numero);
        }
        
        // Reatribui o valor limpo ao campo do record
        numero = limpo;
    }
}
