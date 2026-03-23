package com.grupo51.oficinamecanica.agendamento.controller;

import com.grupo51.oficinamecanica.agendamento.controller.dto.AgendamentoRequestDTO;
import com.grupo51.oficinamecanica.agendamento.model.Agendamento;
import com.grupo51.oficinamecanica.agendamento.model.JanelaServico;
import com.grupo51.oficinamecanica.agendamento.service.AgendamentoService;
import com.grupo51.oficinamecanica.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agendamentos")
@Tag(name = OpenApiConfig.TAG_05_AGENDAMENTOS, description = "Agendamentos de servico")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "5. Realizar agendamento",
            description = "Cria agendamento usando cliente, veiculo e mecanico. O exemplo e valido e pode ser editado." +
                    " Ajuste datas para horario futuro antes de executar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento confirmado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "resposta",
                                    value = "{\"id\":\"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\",\"clienteId\":\"73383053036\",\"veiculoId\":\"ABC1D23\",\"tipo\":\"ANALISE\",\"confirmado\":true}"))),
            @ApiResponse(responseCode = "400", description = "Conflito de horario ou dados invalidos"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<?> realizarAgendamento(
            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Analise (seed)",
                                            summary = "Use cliente e veiculo seed",
                                            value = """
                                                    {
                                                      "clienteId": "73383053036",
                                                      "veiculoId": "ABC1D23",
                                                      "recursoId": "09151522037",
                                                      "dataHoraInicio": "2026-06-10T09:00:00",
                                                      "dataHoraFim": "2026-06-10T11:00:00",
                                                      "tipo": "ANALISE"
                                                    }"""),
                                    @ExampleObject(name = "Execucao",
                                            summary = "Segundo formato valido para o enum",
                                            value = """
                                                    {
                                                      "clienteId": "73383053036",
                                                      "veiculoId": "ABC1D23",
                                                      "recursoId": "09151522037",
                                                      "dataHoraInicio": "2026-06-11T14:00:00",
                                                      "dataHoraFim": "2026-06-11T17:00:00",
                                                      "tipo": "EXECUCAO"
                                                    }""")
                            }))
            @Valid AgendamentoRequestDTO dto, UriComponentsBuilder uriBuilder) {
        var agendamento = new Agendamento(
                null,
                dto.clienteId(),
                dto.veiculoId(),
                new JanelaServico(dto.dataHoraInicio(), dto.dataHoraFim()),
                dto.tipo(),
                dto.recursoId(),
                false
        );

        var confirmado = service.agendar(agendamento);
        var uri = uriBuilder.path("/api/v1/agendamentos/{id}").buildAndExpand(confirmado.getId()).toUri();
        return ResponseEntity.created(uri).body(confirmado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar agendamento por ID",
            description = "Informe o UUID retornado na criacao do agendamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "exemplo",
                                    value = "{\"id\":\"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx\",\"clienteId\":\"73383053036\",\"veiculoId\":\"ABC1D23\",\"tipo\":\"ANALISE\",\"confirmado\":true}"))),
            @ApiResponse(responseCode = "404", description = "Agendamento nao encontrado")
    })
    public ResponseEntity<?> detalhar(
            @Parameter(description = "UUID do agendamento", example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
            @PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id).orElse(null));
    }
}

