package com.grupo37.oficinamecanica.cadastro.infrastructure.repository;

import com.grupo37.oficinamecanica.cadastro.domain.model.Veiculo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "veiculos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VeiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String placa;

    private String modelo;
    private String marca;
    private int ano;
    private String cor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore
    private ClienteEntity dono;

    // Construtor para criar entidade a partir do domínio
    public VeiculoEntity(Veiculo domain) {
        this.id = domain.getId();
        this.placa = domain.getPlaca();
        this.modelo = domain.getModelo();
        this.marca = domain.getMarca();
        this.ano = domain.getAno();
        this.cor = domain.getCor();
        // dono será setado separadamente se necessário
    }

    // Método para converter entidade para domínio
    public Veiculo toDomain() {
        return new Veiculo(
            this.id,
            this.placa,
            this.modelo,
            this.marca,
            this.ano,
            this.cor,
            this.dono != null ? this.dono.toDomain() : null
        );
    }
}
