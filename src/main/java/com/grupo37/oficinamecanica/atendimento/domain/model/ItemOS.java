package com.grupo37.oficinamecanica.atendimento.domain.model;

import com.grupo37.oficinamecanica.estoque.model.Peca;

import java.math.BigDecimal;
import java.util.UUID;

public class ItemOS {
    private UUID id;
    private UUID pecaId;
    private String nomePecaSnapshot; // Nome da peça na hora da venda
    private Integer quantidade;
    private BigDecimal precoNoMomento; // Preço na hora da venda (Histórico)

    public ItemOS(Peca peca, Integer quantidade) {
        this.pecaId = peca.getId();
        this.nomePecaSnapshot = peca.getNome();
        this.quantidade = quantidade;
        this.precoNoMomento = peca.getPreco();
    }

    // Construtor completo para infraestrutura
    public ItemOS(UUID id, UUID pecaId, String nomePecaSnapshot, Integer quantidade, BigDecimal precoNoMomento) {
        this.id = id;
        this.pecaId = pecaId;
        this.nomePecaSnapshot = nomePecaSnapshot;
        this.quantidade = quantidade;
        this.precoNoMomento = precoNoMomento;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getPecaId() { return pecaId; }
    public String getNomePecaSnapshot() { return nomePecaSnapshot; }
    public Integer getQuantidade() { return quantidade; }
    public BigDecimal getPrecoNoMomento() { return precoNoMomento; }
}
