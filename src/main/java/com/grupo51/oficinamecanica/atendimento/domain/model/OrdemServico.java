package com.grupo51.oficinamecanica.atendimento.domain.model;

import com.grupo51.oficinamecanica.cadastro.model.Veiculo;
import com.grupo51.oficinamecanica.comum.exception.BusinessException;
import com.grupo51.oficinamecanica.estoque.model.Peca;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "ordens_servico")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo; // Relacionamento real

    @Column(nullable = false)
    private String descricaoProblema;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOS status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "os_id")
    private List<ItemOS> itens = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    public OrdemServico(Veiculo veiculo, String descricaoProblema) {
        this.veiculo = veiculo;
        this.descricaoProblema = descricaoProblema;
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusOS.RECEBIDA;
        this.valorTotal = BigDecimal.ZERO;
    }

    public void adicionarPeca(Peca peca, int quantidade) {
        if (this.status == StatusOS.FINALIZADA || this.status == StatusOS.ENTREGUE) {
            throw new BusinessException("Não é possível alterar uma OS já encerrada.");
        }

        // Agora a instanciação casa com o novo construtor
        ItemOS novoItem = new ItemOS(peca, quantidade);
        this.itens.add(novoItem);

        // Atualiza o valor total da OS
        BigDecimal valorItem = peca.getPreco().multiply(BigDecimal.valueOf(quantidade));
        this.valorTotal = this.valorTotal.add(valorItem);
    }

    public void atualizarStatus(StatusOS novoStatus) {
        // 1. Validar se a OS já está encerrada
        if (this.status == StatusOS.ENTREGUE) {
            throw new BusinessException("Esta ordem de serviço já foi entregue e não pode mais ser alterada.");
        }

        // 2. Exemplo de regra de negócio: não pode pular do RECEBIDA direto para FINALIZADA
        if (this.status == StatusOS.RECEBIDA &&
                (novoStatus == StatusOS.FINALIZADA || novoStatus == StatusOS.ENTREGUE)) {
            throw new BusinessException("A OS precisa passar por diagnóstico antes de ser finalizada ou entregue.");
        }

        // 3. Bloqueio de retrocesso
        if (novoStatus.ordinal() < this.status.ordinal()) {
            throw new BusinessException("Não é permitido retornar a Ordem de Serviço para um status anterior: " + this.status);
        }

        this.status = novoStatus;
    }
}
