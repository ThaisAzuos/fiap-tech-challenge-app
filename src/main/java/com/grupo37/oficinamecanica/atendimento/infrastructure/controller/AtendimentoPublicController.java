package com.grupo37.oficinamecanica.atendimento.infrastructure.controller;

import com.grupo37.oficinamecanica.atendimento.application.usecase.AtendimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/atendimento")
@Tag(name = "Atendimento Público", description = "Endpoints públicos para aprovação externa de orçamentos")
public class AtendimentoPublicController {

    private final AtendimentoService atendimentoService;

    public AtendimentoPublicController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @PostMapping("/os/{osId}/aprovacao")
    @Operation(summary = "Aprovar orçamento externamente",
            description = "Endpoint público para aprovação de orçamento sem autenticação. " +
                    "Use este link para aprovar o orçamento da OS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento aprovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "OS não está aguardando aprovação"),
            @ApiResponse(responseCode = "404", description = "OS não encontrada")
    })
    public ResponseEntity<String> aprovarOrcamentoPublico(
            @Parameter(description = "UUID da O.S.", example = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
            @PathVariable UUID osId) {
        atendimentoService.aprovarOrcamento(osId);
        return ResponseEntity.ok("Orçamento aprovado com sucesso! A execução do serviço será iniciada.");
    }
}
