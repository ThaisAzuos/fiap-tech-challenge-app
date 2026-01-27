package com.grupo51.oficinamecanica.cadastro.model.dto;

public record ClienteDTO(
        String nome,
        String cpf,
        String email,
        String telefone,
        EnderecoDTO endereco
) {}
