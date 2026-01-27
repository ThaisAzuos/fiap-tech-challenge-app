package com.grupo51.oficinamecanica.cadastro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "veiculos")
@Getter
public class Veiculo {
    @Id
    private String placa; // ID Natural

    private String modelo;
    private String marca;
    private int ano;
    private String cor;

    protected Veiculo() {} // JPA

    public Veiculo(String placa, String modelo, String marca, int ano, String cor) {
        this.placa = new Placa(placa).valor(); // Validação imediata
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.cor = cor;
        validarAno();
    }

    private void validarAno() {
        int anoAtual = java.time.Year.now().getValue();
        if (ano < 1900 || ano > anoAtual + 1) {
            throw new IllegalArgumentException("Ano do veículo é inválido.");
        }
    }
}
