package com.grupo51.oficinamecanica.cadastro.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Endereco do cliente")
public record EnderecoDTO(
        @Schema(example = "Avenida Paulista")
        String logradouro,
        @Schema(example = "1000")
        String numero,
        @Schema(example = "Apto 201")
        String complemento,
        @Schema(example = "Bela Vista")
        String bairro,
        @Schema(example = "Sao Paulo")
        String cidade,
        @Schema(example = "SP")
        String uf,
        @Schema(example = "01310100")
        String cep
) {}
