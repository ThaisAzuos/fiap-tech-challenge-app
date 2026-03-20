package com.grupo51.oficinamecanica.cadastro.controller.dto;

import com.grupo51.oficinamecanica.cadastro.model.Cargo;
import com.grupo51.oficinamecanica.cadastro.model.Especialidade;
import com.grupo51.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo51.oficinamecanica.comum.validation.NoXss;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FuncionarioCadastroDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        @NoSqlInjection
        @NoXss
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos")
        @NoSqlInjection
        String cpf,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 16, message = "A senha deve ter entre 8 e 16 caracteres.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
                message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial.")
        String senha,

        @NotNull(message = "Cargo é obrigatório")
        Cargo cargo,

        Especialidade especialidade,

        @Size(max = 20, message = "Registro funcional deve ter no máximo 20 caracteres")
        @NoSqlInjection
        String registroFuncional
) {
}
