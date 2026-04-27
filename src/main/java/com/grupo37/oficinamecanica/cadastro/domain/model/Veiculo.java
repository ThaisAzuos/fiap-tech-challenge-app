package com.grupo37.oficinamecanica.cadastro.domain.model;

import java.util.UUID;

public class Veiculo {

    private UUID id;
    private String placa;
    private String modelo;
    private String marca;
    private int ano;
    private String cor;
    private Cliente dono;

    // Construtor de domínio
    public Veiculo(Placa placa, String modelo, String marca, Integer ano, String cor, Cliente dono) {
        this.placa = placa.valor();
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.cor = cor;
        this.dono = dono;
        validarAno();
    }

    // Construtor completo para infraestrutura
    public Veiculo(UUID id, String placa, String modelo, String marca, int ano, String cor, Cliente dono) {
        this.id = id;
        this.placa = placa;
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.cor = cor;
        this.dono = dono;
    }

    private void validarAno() {
        int anoAtual = java.time.Year.now().getValue();
        if (ano < 1900 || ano > anoAtual + 1) {
            throw new IllegalArgumentException("Ano do veículo é inválido.");
        }
    }

    // Getters
    public UUID getId() { return id; }
    public String getPlaca() { return placa; }
    public String getModelo() { return modelo; }
    public String getMarca() { return marca; }
    public int getAno() { return ano; }
    public String getCor() { return cor; }
    public Cliente getDono() { return dono; }
}
