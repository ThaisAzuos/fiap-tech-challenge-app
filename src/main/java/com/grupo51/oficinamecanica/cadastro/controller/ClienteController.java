package com.grupo51.oficinamecanica.cadastro.controller;

import com.grupo51.oficinamecanica.cadastro.model.Cliente;
import com.grupo51.oficinamecanica.cadastro.model.dto.ClienteDTO;
import com.grupo51.oficinamecanica.cadastro.service.CadastroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final CadastroService cadastroService;

    public ClienteController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @PostMapping
    public ResponseEntity<Cliente> criarCliente(@RequestBody @Valid ClienteDTO dto, UriComponentsBuilder uriBuilder) {
        Cliente novoCliente = cadastroService.salvarCliente(dto);
        var uri = uriBuilder.path("/api/v1/clientes/{cpf}").buildAndExpand(novoCliente.getCpf()).toUri();
        return ResponseEntity.created(uri).body(novoCliente);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(cadastroService.listarTodos());
    }
}
