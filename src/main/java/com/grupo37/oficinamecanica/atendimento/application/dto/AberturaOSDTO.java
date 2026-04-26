package com.grupo37.oficinamecanica.atendimento.application.dto;

import com.grupo37.oficinamecanica.comum.validation.NoSqlInjection;
import com.grupo37.oficinamecanica.comum.validation.NoXss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "Payload para abertura de ordem de servico")
public record AberturaOSDTO(
        @Schema(description = "Placa do veiculo", example = "ABC1D23")
        @NotBlank(message = "Placa do veículo é obrigatória")
        @Pattern(regexp = "^[A-Z]{3}\\d[A-Z]\\d{2}$|^[A-Z]{3}-?\\d{4}$",
                 message = "Placa deve ser válida")
        @NoSqlInjection
        String placa,

        @Schema(description = "Relato inicial do problema", example = "Barulho na transmissao ao acelerar. Falha intermitente.")
        @NotBlank(message = "Descrição do problema é obrigatória")
        @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
        @NoSqlInjection
        @NoXss
        String descricaoProblema,

        @Schema(description = "Lista de peças a serem incluídas na OS (opcional)", example = "[{\"pecaId\":\"uuid\",\"quantidade\":1}]")
        @Valid
        List<IncluirPecaDTO> pecas,

        @Schema(description = "Lista de serviços a serem incluídos na OS (opcional)", example = "[{\"servicoId\":\"uuid\"}]")
        @Valid
        List<IncluirServicoDTO> servicos
) {}
