package com.grupo37.oficinamecanica.cadastro.controller.dto;

import com.grupo37.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo37.oficinamecanica.comum.validation.NoXss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Payload para cadastro de cliente")
public record ClienteDTO(
        @Schema(description = "Nome completo", example = "Marina Oliveira")
        @NotBlank(message = "Nome e obrigatorio")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        @NoSqlInjection
        @NoXss
        String nome,

        @Schema(description = "CPF sem pontuacao", example = "52998224725")
        @NotBlank(message = "CPF e obrigatorio")
        @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 digitos")
        @NoSqlInjection
        String cpf,

        @Schema(description = "Email do cliente", example = "marina.oliveira@email.com.br")
        @NotBlank(message = "Email e obrigatorio")
        @Email(message = "Email deve ser valido")
        @NoXss
        String email,

        @Schema(description = "Telefone com DDD", example = "11987654321")
        @NotBlank(message = "Telefone e obrigatorio")
        @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve ter 10 ou 11 digitos")
        @NoSqlInjection
        String telefone,

        @Schema(description = "Endereco do cliente")
        @NotNull(message = "Endereco e obrigatorio")
        EnderecoDTO endereco
) {}