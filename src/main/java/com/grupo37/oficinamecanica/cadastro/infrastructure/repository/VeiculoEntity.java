package com.grupo37.oficinamecanica.cadastro.infrastructure.repository;

import com.grupo37.oficinamecanica.cadastro.domain.model.Placa;
import com.grupo37.oficinamecanica.cadastro.domain.model.Veiculo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "veiculos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VeiculoEntity {

    @Id
    @Column(nullable = false, length = 7)
    private String placa; // Alinhado com o ID Natural do domínio

    private String modelo;
    private String marca;
    private int ano;
    private String cor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore
    private ClienteEntity dono;

    // Construtor para criar entidade a partir do domínio (Salvar no Banco)
    public VeiculoEntity(Veiculo domain) {
        this.placa = domain.getPlaca();
        this.modelo = domain.getModelo();
        this.marca = domain.getMarca();
        this.ano = domain.getAno();
        this.cor = domain.getCor();

        // Mapeia o dono se ele estiver presente no domínio
        this.dono = domain.getDono() != null ? new ClienteEntity(domain.getDono()) : null;
    }

    // Dentro de VeiculoEntity.java

    public Veiculo toDomain() {
        return new Veiculo(
                new Placa(this.placa), // Ajustado: Convertendo String do banco para o VO do Domínio
                this.modelo,
                this.marca,
                this.ano,
                this.cor,
                this.dono != null ? this.dono.toDomain() : null
        );
    }
}