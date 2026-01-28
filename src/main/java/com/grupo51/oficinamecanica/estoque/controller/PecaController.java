package com.grupo51.oficinamecanica.estoque.controller;

import com.grupo51.oficinamecanica.estoque.model.Peca;
import com.grupo51.oficinamecanica.estoque.service.PecaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pecas")
public class PecaController {

    private final PecaService pecaService;

    public PecaController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    @PostMapping
    public ResponseEntity<Peca> criarPeca(@RequestBody PecaDTO dto) {
        Peca pecaSalva = pecaService.salvarPeca(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pecaSalva);
    }

    @GetMapping
    public ResponseEntity<List<Peca>> listar() {
        return ResponseEntity.ok(pecaService.listarTodas());
    }
}