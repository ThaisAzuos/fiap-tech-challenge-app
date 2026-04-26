package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.domain.model.ItemOS;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico_itens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOSEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID pecaId;
    private String nomePecaSnapshot; // Nome da peça na hora da venda
    private Integer quantidade;
    private BigDecimal precoNoMomento; // Preço na hora da venda (Histórico)

    // Construtor para criar entidade a partir do domínio
    public ItemOSEntity(ItemOS domain) {
        this.id = domain.getId();
        this.pecaId = domain.getPecaId();
        this.nomePecaSnapshot = domain.getNomePecaSnapshot();
        this.quantidade = domain.getQuantidade();
        this.precoNoMomento = domain.getPrecoNoMomento();
    }

    // Método para converter entidade para domínio
    public ItemOS toDomain() {
        return new ItemOS(
            this.id,
            this.pecaId,
            this.nomePecaSnapshot,
            this.quantidade,
            this.precoNoMomento
        );
    }
}
