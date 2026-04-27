package com.grupo37.oficinamecanica.cadastro.domain.model;

public record Endereco(
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String cep
) {
    public Endereco {
        if (logradouro == null || logradouro.isBlank()) {
            throw new IllegalArgumentException("Logradouro é obrigatório");
        }
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Número é obrigatório");
        }
        if (bairro == null || bairro.isBlank()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }
        if (cidade == null || cidade.isBlank()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
        if (cep == null || !cep.matches("\\d{8}")) {
            throw new IllegalArgumentException("CEP deve ter exatamente 8 dígitos numéricos");
        }
    }
}
