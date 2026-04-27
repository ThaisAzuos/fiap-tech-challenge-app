package com.grupo37.oficinamecanica.estoque.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Peca {

    private UUID id;
    private String nome;
    private BigDecimal preco;
    private Integer quantidadeEstoque;

    // Construtor de domínio
    public Peca(String nome, BigDecimal preco, Integer quantidadeEstoque) {
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    // Construtor completo para infraestrutura
    public Peca(UUID id, String nome, BigDecimal preco, Integer quantidadeEstoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    // Getters
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getPreco() { return preco; }
    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
}
