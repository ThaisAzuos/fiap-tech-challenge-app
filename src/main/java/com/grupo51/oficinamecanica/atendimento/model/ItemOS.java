package com.grupo51.oficinamecanica.atendimento.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico_itens")
@Getter
public class ItemOS {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // O Hibernate cuidará da geração do UUID
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private UUID pecaId;
    private String nomePecaSnapshot;
    private Integer quantidade;
    private BigDecimal precoNoMomento;

    protected ItemOS() {}

    public ItemOS(UUID pecaId, String nomePeca, Integer quantidade, BigDecimal preco) {
        this.pecaId = pecaId;
        this.nomePecaSnapshot = nomePeca;
        this.quantidade = quantidade;
        this.precoNoMomento = preco;
    }
}