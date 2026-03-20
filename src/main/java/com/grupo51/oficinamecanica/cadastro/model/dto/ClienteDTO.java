package com.grupo51.oficinamecanica.cadastro.model.dto;

import com.grupo51.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo51.oficinamecanica.comum.validation.NoXss;
import jakarta.validation.constraints.*;

public record ClienteDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        @NoSqlInjection
        @NoXss
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos")
        @NoSqlInjection
        String cpf,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        @NoXss
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve ter 10 ou 11 dígitos")
        @NoSqlInjection
        String telefone,

        @NotNull(message = "Endereço é obrigatório")
        EnderecoDTO endereco
) {}
