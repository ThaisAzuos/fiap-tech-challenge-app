package com.grupo51.oficinamecanica.atendimento.controller;

import com.grupo51.oficinamecanica.atendimento.controller.dto.AberturaOSDTO;
import com.grupo51.oficinamecanica.atendimento.controller.dto.IncluirPecaDTO;
import com.grupo51.oficinamecanica.atendimento.controller.dto.OrdemServicoDetalhesDTO;
import com.grupo51.oficinamecanica.atendimento.model.OrdemServico;
import com.grupo51.oficinamecanica.atendimento.service.AtendimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/atendimento")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @PostMapping("/os")
    public ResponseEntity<OrdemServico> abrirOS(@RequestBody AberturaOSDTO dto) {
        OrdemServico os = atendimentoService.abrirOrdem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(os);
    }

    @PostMapping("/os/{osId}/pecas")
    public ResponseEntity<Void> incluirPeca(
            @PathVariable UUID osId,
            @RequestBody IncluirPecaDTO dto) {

        atendimentoService.incluirPecaNaOS(osId, dto.pecaId(), dto.quantidade());

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/os/{osId}")
    public ResponseEntity<OrdemServicoDetalhesDTO> buscarDetalhes(@PathVariable UUID osId) {
        return ResponseEntity.ok(atendimentoService.consultarDetalhes(osId));
    }
}
