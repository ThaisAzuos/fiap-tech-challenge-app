package com.grupo37.oficinamecanica.atendimento.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class ItemServicoOS {
    private UUID id;
    private UUID servicoId;
    private String nomeServicoSnapshot;
    private BigDecimal precoNoMomento;

    public ItemServicoOS(Servico servico) {
        this.servicoId = servico.getId();
        this.nomeServicoSnapshot = servico.getNome();
        this.precoNoMomento = servico.getPreco();
    }

    // Construtor completo para infraestrutura
    public ItemServicoOS(UUID id, UUID servicoId, String nomeServicoSnapshot, BigDecimal precoNoMomento) {
        this.id = id;
        this.servicoId = servicoId;
        this.nomeServicoSnapshot = nomeServicoSnapshot;
        this.precoNoMomento = precoNoMomento;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getServicoId() { return servicoId; }
    public String getNomeServicoSnapshot() { return nomeServicoSnapshot; }
    public BigDecimal getPrecoNoMomento() { return precoNoMomento; }
}
