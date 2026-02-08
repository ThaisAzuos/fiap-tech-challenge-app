package com.grupo51.oficinamecanica.agendamento.controller;

import com.grupo51.oficinamecanica.agendamento.controller.dto.AgendamentoRequestDTO;
import com.grupo51.oficinamecanica.agendamento.model.Agendamento;
import com.grupo51.oficinamecanica.agendamento.model.JanelaServico;
import com.grupo51.oficinamecanica.agendamento.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService service;

    @PostMapping
    public ResponseEntity<?> realizarAgendamento(@RequestBody AgendamentoRequestDTO dto, UriComponentsBuilder uriBuilder) {
        var agendamento = new Agendamento(null, dto.clienteId(), dto.veiculoId(),
                new JanelaServico(dto.dataHoraInicio(), dto.dataHoraFim()),
                dto.tipo(), dto.recursoId(), false);

        var agendamentoConfirmado = service.agendar(agendamento);
        var uri = uriBuilder.path("/agendamentos/{id}").buildAndExpand(agendamentoConfirmado.getId()).toUri();

        return ResponseEntity.created(uri).body(agendamentoConfirmado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalhar(@PathVariable UUID id) {
        // Implementação simples de busca para conferência
        return ResponseEntity.ok(service.buscarPorId(id).orElse(null));
    }
}
