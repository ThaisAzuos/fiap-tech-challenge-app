package com.grupo51.oficinamecanica.cadastro.model;

import jakarta.persistence.Embeddable;

@Embeddable
public record Endereco(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String cep
) {}
