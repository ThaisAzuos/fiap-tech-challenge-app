package com.grupo37.oficinamecanica.atendimento.infrastructure.controller;

import com.grupo37.oficinamecanica.atendimento.application.dto.AberturaOSDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.AberturaOSResponseDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.IncluirPecaDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.IncluirServicoDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.MensagemResponse;
import com.grupo37.oficinamecanica.atendimento.application.dto.OrdemServicoDetalhesDTO;
import com.grupo37.oficinamecanica.atendimento.application.dto.OrdemServicoListDTO;
import com.grupo37.oficinamecanica.atendimento.application.usecase.AtendimentoService;
import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.atendimento.domain.model.StatusOS;
import com.grupo37.oficinamecanica.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/atendimento")
@Tag(name = OpenApiConfig.TAG_07_ATENDIMENTO,
        description = "Fluxo de O.S.: abrir, avancar status, consultar, incluir peca, aprovar e listar")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @PostMapping("/os")
    @Operation(summary = "7. Abrir Ordem de Servico",
            description = "Primeiro passo do atendimento. Use placa seed ABC1D23 ou uma placa criada no passo 2." +
                    " Anote o id (UUID) retornado para os proximos passos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "O.S. aberta - copie o campo id",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "resposta",
                                    value = "{\"id\":\"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\",\"placa\":\"ABC1D23\",\"status\":\"RECEBIDA\",\"dataAbertura\":\"2026-03-21T10:30:00\"}"))),
            @ApiResponse(responseCode = "400", description = "Placa invalida ou nao encontrada"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<AberturaOSResponseDTO> abrirOS(
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Veiculo seed (ABC1D23)",
                                            summary = "Executa direto com dados seed",
                                            value = "{\"placa\":\"ABC1D23\",\"descricaoProblema\":\"Barulho na transmissao ao acelerar. Falha intermitente.\"}"),
                                    @ExampleObject(name = "Veiculo criado no passo 2",
                                            summary = "Use a placa cadastrada em 02. Veiculos",
                                            value = "{\"placa\":\"DEF4G56\",\"descricaoProblema\":\"Pedal de freio esponjoso. Possivel ar no sistema hidraulico.\"}"),
                                    @ExampleObject(name = "Com pecas",
                                            summary = "Abertura com peças incluídas",
                                            value = "{\"placa\":\"ABC1D23\",\"descricaoProblema\":\"Troca de oleo e filtro.\",\"pecas\":[{\"pecaId\":\"uuid-da-peca\",\"quantidade\":1}]}"),
                                    @ExampleObject(name = "Com servicos",
                                            summary = "Abertura com serviços incluídos",
                                            value = "{\"placa\":\"ABC1D23\",\"descricaoProblema\":\"Revisao completa.\",\"servicos\":[{\"servicoId\":\"uuid-do-servico\"}]}")
                            }))
            @Valid AberturaOSDTO dto) {
        OrdemServico os = atendimentoService.abrirOrdem(dto);
        AberturaOSResponseDTO response = new AberturaOSResponseDTO(
                os.getId(),
                dto.placa(),
                os.getStatus().name(),
                os.getDataAbertura()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/os/{osId}/status")
    @Operation(summary = "8. Avancar status da O.S.",
            description = "Fluxo normal: RECEBIDA -> EM_DIAGNOSTICO -> AGUARDANDO_APROVACAO -> EM_EXECUCAO -> FINALIZADA -> ENTREGUE." +
                    " Cancelamento: qualquer status -> CANCELADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "sucesso", value = "{\"mensagem\":\"Status atualizado com sucesso para CANCELADA\"}"))),
            @ApiResponse(responseCode = "400", description = "Transicao de status invalida"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<MensagemResponse> mudarStatus(
            @Parameter(description = "UUID da O.S.", example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
            @PathVariable UUID osId,
            @Parameter(description = "Novo status",
                    example = "EM_DIAGNOSTICO",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                            allowableValues = {
                                    "RECEBIDA",
                                    "EM_DIAGNOSTICO",
                                    "AGUARDANDO_APROVACAO",
                                    "EM_EXECUCAO",
                                    "FINALIZADA",
                                    "ENTREGUE",
                                    "CANCELADA"
                            }))
            @RequestParam StatusOS novoStatus) {
        atendimentoService.atualizarStatus(osId, novoStatus);
        
        String texto = novoStatus == StatusOS.CANCELADA 
                ? "Ordem de Serviço cancelada com sucesso." 
                : "Status atualizado com sucesso para " + novoStatus;
                
        return ResponseEntity.ok(new MensagemResponse(texto));
    }

    @GetMapping("/os/{osId}")
    @Operation(summary = "9. Consultar detalhes da O.S.",
            description = "Retorna dados completos da O.S., incluindo status e itens de peca.")
    @ApiResponse(responseCode = "200", description = "Detalhes da O.S.",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "exemplo",
                            value = "{\"id\":\"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\",\"placa\":\"ABC1D23\",\"status\":\"EM_DIAGNOSTICO\",\"itens\":[]}")))
    public ResponseEntity<OrdemServicoDetalhesDTO> buscarDetalhes(
            @Parameter(description = "UUID da O.S.", example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
            @PathVariable UUID osId) {
        return ResponseEntity.ok(atendimentoService.consultarDetalhes(osId));
    }

    @PostMapping("/os/{osId}/pecas")
    @Operation(summary = "10. Incluir peca na O.S.",
            description = "Adiciona peca na O.S. usando o UUID retornado no passo 6 (cadastrar/listar pecas).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Peca adicionada a O.S."),
            @ApiResponse(responseCode = "404", description = "O.S. ou peca nao encontrada"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<Void> incluirPeca(
            @Parameter(description = "UUID da O.S.", example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
            @PathVariable UUID osId,
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "incluir peca",
                                    summary = "Substitua pecaId por um UUID real do passo 6",
                                    value = "{\"pecaId\":\"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\",\"quantidade\":1}")))
            @Valid IncluirPecaDTO dto) {
        atendimentoService.incluirPecaNaOS(osId, dto.pecaId(), dto.quantidade());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/os/{osId}/servicos")
    @Operation(summary = "10.1. Incluir servico na O.S.",
            description = "Adiciona servico na O.S. usando o UUID do servico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Servico adicionado a O.S."),
            @ApiResponse(responseCode = "404", description = "O.S. ou servico nao encontrado"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<Void> incluirServico(
            @Parameter(description = "UUID da O.S.", example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
            @PathVariable UUID osId,
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "incluir servico",
                                    summary = "Substitua servicoId por um UUID real",
                                    value = "{\"servicoId\":\"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\"}")))
            @Valid IncluirServicoDTO dto) {
        atendimentoService.incluirServicoNaOS(osId, dto.servicoId());
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/os/{osId}/aprovacao")
    @Operation(summary = "11. Aprovar orcamento da O.S.",
            description = "Aprovacao deve ser feita apos a O.S. chegar em AGUARDANDO_APROVACAO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Orcamento aprovado"),
            @ApiResponse(responseCode = "404", description = "O.S. nao encontrada"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<Void> aprovarOrcamento(
            @Parameter(description = "UUID da O.S.", example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
            @PathVariable UUID osId) {
        atendimentoService.aprovarOrcamento(osId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/os")
    @Operation(summary = "12. Listar ordens de servico (paginado)",
            description = "Lista paginada para acompanhamento final do fluxo.")
    @ApiResponse(responseCode = "200", description = "Lista paginada de O.S.",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "exemplo",
                            value = "{\"content\":[{\"id\":\"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\",\"placa\":\"ABC1D23\",\"status\":\"EM_DIAGNOSTICO\",\"dataAbertura\":\"2026-03-21T10:30:00\"}],\"totalElements\":1}")))
    public ResponseEntity<Page<OrdemServicoListDTO>> listarOS(Pageable pageable) {
        return ResponseEntity.ok(atendimentoService.listarOrdensServico(pageable));
    }
}
