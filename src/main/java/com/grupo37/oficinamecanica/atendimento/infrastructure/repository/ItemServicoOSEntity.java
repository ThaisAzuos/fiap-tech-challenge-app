package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.domain.model.ItemServicoOS;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico_servicos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemServicoOSEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID servicoId;
    private String nomeServicoSnapshot;
    private BigDecimal precoNoMomento;

    // Construtor para criar entidade a partir do domínio
    public ItemServicoOSEntity(ItemServicoOS domain) {
        this.id = domain.getId();
        this.servicoId = domain.getServicoId();
        this.nomeServicoSnapshot = domain.getNomeServicoSnapshot();
        this.precoNoMomento = domain.getPrecoNoMomento();
    }

    // Método para converter entidade para domínio
    public ItemServicoOS toDomain() {
        return new ItemServicoOS(
            this.id,
            this.servicoId,
            this.nomeServicoSnapshot,
            this.precoNoMomento
        );
    }
}
