package com.grupo37.oficinamecanica.atendimento.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Servico {

    private UUID id;
    private String nome;
    private BigDecimal preco;
    private String descricao;

    // Construtor
    public Servico(String nome, BigDecimal preco, String descricao) {
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
    }

    // Construtor completo para infraestrutura
    public Servico(UUID id, String nome, BigDecimal preco, String descricao) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
    }

    // Getters
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getPreco() { return preco; }
    public String getDescricao() { return descricao; }
}
