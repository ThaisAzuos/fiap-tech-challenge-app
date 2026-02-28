package com.grupo51.oficinamecanica.cadastro.controller;

import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.cadastro.model.dto.VeiculoDTO;
import com.grupo51.oficinamecanica.cadastro.service.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping
    public ResponseEntity<Veiculo> criarVeiculo(@RequestBody @Valid VeiculoDTO dto, UriComponentsBuilder uriBuilder) {
        Veiculo novoVeiculo = veiculoService.salvarVeiculo(dto);
        var uri = uriBuilder.path("/api/v1/veiculos/{placa}").buildAndExpand(novoVeiculo.getPlaca()).toUri();
        return ResponseEntity.created(uri).body(novoVeiculo);
    }

    @GetMapping("/dono/{cpf}")
    public ResponseEntity<List<Veiculo>> listarPorDono(@PathVariable String cpf) {
        List<Veiculo> veiculos = veiculoService.listarPorCpfDono(cpf);
        return ResponseEntity.ok(veiculos);
    }
}
