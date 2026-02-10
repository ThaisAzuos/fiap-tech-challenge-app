package com.grupo51.oficinamecanica.atendimento.model;

import com.grupo51.oficinamecanica.estoque.model.Peca;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico_itens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA Friendly
public class ItemOS {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
}