package com.grupo51.oficinamecanica.cadastro.controller.dto;

import com.grupo51.oficinamecanica.cadastro.model.Cargo;
import com.grupo51.oficinamecanica.cadastro.model.Especialidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FuncionarioCadastroDTO(
        @NotBlank
        String nome,

        @NotBlank
        String cpf,

        @NotBlank
        @Size(min = 8, max = 16, message = "A senha deve ter entre 8 e 16 caracteres.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
                message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial.")
        String senha,

        @NotNull
        Cargo cargo,

        Especialidade especialidade,

        String registroFuncional
) {
}
