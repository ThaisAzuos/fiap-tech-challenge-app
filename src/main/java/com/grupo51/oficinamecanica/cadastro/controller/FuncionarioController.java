package com.grupo51.oficinamecanica.cadastro.controller;

import com.grupo51.oficinamecanica.cadastro.model.Funcionario;
import com.grupo51.oficinamecanica.cadastro.service.FuncionarioService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/funcionarios")
public class FuncionarioController {
    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @PostMapping
    public Funcionario cadastrar(@RequestBody Funcionario funcionario) {
        return service.salvar(funcionario);
    }

    @GetMapping
    public List<Funcionario> listar() {
        return service.listarTodos();
    }
}
