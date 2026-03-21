package com.grupo51.oficinamecanica.cadastro.controller.dto;

import com.grupo51.oficinamecanica.cadastro.model.Cargo;
import com.grupo51.oficinamecanica.cadastro.model.Especialidade;
import com.grupo51.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo51.oficinamecanica.comum.validation.NoXss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para cadastro de funcionario")
public record FuncionarioCadastroDTO(
        @Schema(description = "Nome completo", example = "Carlos Eduardo Souza")
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        @NoSqlInjection
        @NoXss
        String nome,

        @Schema(description = "CPF sem pontuacao", example = "71428793860")
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos")
        @NoSqlInjection
        String cpf,

        @Schema(description = "Senha com maiuscula, minuscula, numero e simbolo", example = "Senha@2024")
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 16, message = "A senha deve ter entre 8 e 16 caracteres.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
                message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial.")
        String senha,

        @Schema(description = "Cargo", allowableValues = {"ATENDENTE", "GERENTE", "MECANICO"}, example = "MECANICO")
        @NotNull(message = "Cargo é obrigatório")
        Cargo cargo,

        @Schema(description = "Especialidade tecnica (normalmente para mecanico)", allowableValues = {"MOTORES", "SUSPENSAO", "ELETRICA", "GERAL"}, example = "MOTORES")
        Especialidade especialidade,

        @Schema(description = "Registro funcional interno", example = "MF-010")
        @Size(max = 20, message = "Registro funcional deve ter no máximo 20 caracteres")
        @NoSqlInjection
        String registroFuncional
) {
}
