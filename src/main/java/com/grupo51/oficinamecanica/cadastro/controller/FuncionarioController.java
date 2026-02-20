package com.grupo51.oficinamecanica.cadastro.controller;

import com.grupo51.oficinamecanica.cadastro.controller.dto.FuncionarioCadastroDTO;
import com.grupo51.oficinamecanica.cadastro.model.Funcionario;
import com.grupo51.oficinamecanica.cadastro.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funcionarios")
public class FuncionarioController {
    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Funcionario> cadastrar(@RequestBody @Valid FuncionarioCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var funcionario = service.salvar(dados);
        var uri = uriBuilder.path("/api/v1/funcionarios/{cpf}").buildAndExpand(funcionario.getCpf()).toUri();
        return ResponseEntity.created(uri).body(funcionario);
    }

    @GetMapping
    public List<Funcionario> listar() {
        return service.listarTodos();
    }
}
