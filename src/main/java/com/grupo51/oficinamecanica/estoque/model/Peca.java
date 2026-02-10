package com.grupo51.oficinamecanica.estoque.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pecas")
@Getter
public class Peca {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String nome;
    private BigDecimal preco;
    private Integer quantidadeEstoque;

    protected Peca() {}

    public Peca(String nome, BigDecimal preco, Integer quantidadeEstoque) {
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

}
