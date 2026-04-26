package com.grupo37.oficinamecanica.atendimento.infrastructure.repository;

import com.grupo37.oficinamecanica.atendimento.domain.model.ItemOS;
import com.grupo37.oficinamecanica.atendimento.domain.model.ItemServicoOS;
import com.grupo37.oficinamecanica.atendimento.domain.model.OrdemServico;
import com.grupo37.oficinamecanica.atendimento.domain.model.StatusOS;
import com.grupo37.oficinamecanica.cadastro.model.Veiculo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "ordens_servico")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Column(nullable = false)
    private String descricaoProblema;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOS status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "os_id")
    private List<ItemOSEntity> itens = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "os_id")
    private List<ItemServicoOSEntity> servicos = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column
    private LocalDateTime dataCancelamento;

    @Column(columnDefinition = "TEXT")
    private String motivoCancelamento;

    @Column
    private LocalDateTime dataConclusao;

    // Construtor para criar entidade a partir do domínio
    public OrdemServicoEntity(OrdemServico domain) {
        this.id = domain.getId();
        this.veiculo = domain.getVeiculo();
        this.descricaoProblema = domain.getDescricaoProblema();
        this.dataAbertura = domain.getDataAbertura();
        this.status = domain.getStatus();
        this.itens = domain.getItens().stream()
                .map(ItemOSEntity::new)
                .collect(Collectors.toList());
        this.servicos = domain.getServicos().stream()
                .map(ItemServicoOSEntity::new)
                .collect(Collectors.toList());
        this.valorTotal = domain.getValorTotal();
        this.dataCancelamento = domain.getDataCancelamento();
        this.motivoCancelamento = domain.getMotivoCancelamento();
        this.dataConclusao = domain.getDataConclusao();
    }

    // Método para converter entidade para domínio
    public OrdemServico toDomain() {
        List<ItemOS> domainItens = this.itens.stream()
                .map(ItemOSEntity::toDomain)
                .collect(Collectors.toList());

        List<ItemServicoOS> domainServicos = this.servicos.stream()
                .map(ItemServicoOSEntity::toDomain)
                .collect(Collectors.toList());

        return new OrdemServico(
            this.id,
            this.veiculo,
            this.descricaoProblema,
            this.dataAbertura,
            this.status,
            domainItens,
            domainServicos,
            this.valorTotal,
            this.dataCancelamento,
            this.motivoCancelamento,
            this.dataConclusao
        );
    }
}
