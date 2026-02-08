package com.grupo51.oficinamecanica.cadastro.controller;

import com.grupo51.oficinamecanica.cadastro.model.Mecanico;
import com.grupo51.oficinamecanica.cadastro.service.MecanicoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mecanicos")
public class MecanicoController {
    private final MecanicoService service;

    public MecanicoController(MecanicoService service) {
        this.service = service;
    }

    @PostMapping
    public Mecanico cadastrar(@RequestBody Mecanico mecanico) {
        return service.salvar(mecanico);
    }

    @GetMapping
    public List<Mecanico> listar() {
        return service.listarTodos();
    }
}
