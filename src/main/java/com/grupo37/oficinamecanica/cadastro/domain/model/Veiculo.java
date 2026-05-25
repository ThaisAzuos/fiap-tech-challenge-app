package com.grupo37.oficinamecanica.cadastro.domain.model;

import lombok.Getter;

@Getter
public class Veiculo {
    private String placa;
    private String modelo;
    private String marca;
    private int ano;
    private String cor;
    private Cliente dono;

    // Construtor rico do domínio (Validações e regras de negócio)
    public Veiculo(Placa placa, String modelo, String marca, Integer ano, String cor, Cliente dono) {
        this.placa = placa.valor();
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.cor = cor;
        this.dono = dono;
        validarAno();
    }

    private void validarAno() {
        int anoAtual = java.time.Year.now().getValue();
        if (ano < 1900 || ano > anoAtual + 1) {
            throw new IllegalArgumentException("Ano do veículo é inválido.");
        }
    }
}