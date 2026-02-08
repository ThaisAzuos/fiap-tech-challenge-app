package com.grupo51.oficinamecanica.cadastro.model.dto;

public record EnderecoDTO(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
         String uf,
        String cep
){}
