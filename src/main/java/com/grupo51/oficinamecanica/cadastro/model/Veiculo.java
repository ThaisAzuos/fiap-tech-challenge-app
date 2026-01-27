package com.grupo51.oficinamecanica.cadastro.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "veiculos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Veiculo {
    @Id
    private String placa; // ID Natural

    private String modelo;
    private String marca;
    private int ano;
    private String cor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore // Evita que o dono venha detalhado no JSON do veículo, se desejar
    private Cliente dono;

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
