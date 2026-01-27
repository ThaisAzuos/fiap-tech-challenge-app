package com.grupo51.oficinamecanica.cadastro.controller;

import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.cadastro.model.dto.VeiculoDTO;
import com.grupo51.oficinamecanica.cadastro.service.VeiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    // Injeção via construtor
    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping
    public ResponseEntity<Veiculo> criarVeiculo(@RequestBody VeiculoDTO dto) {
        Veiculo novoVeiculo = veiculoService.salvarVeiculo(dto);

        // Retorna 201 Created e o objeto criado no corpo da resposta
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoVeiculo.getPlaca())
                .toUri();

        return ResponseEntity.created(uri).body(novoVeiculo);
    }

    @GetMapping("/dono/{cpf}")
    public ResponseEntity<List<Veiculo>> listarPorDono(@PathVariable String cpf) {
        List<Veiculo> veiculos = veiculoService.listarPorCpfDono(cpf);
        return ResponseEntity.ok(veiculos);
    }
}
